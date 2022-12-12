package ru.mirea.web3.web3javaclient.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.Post;
import ru.mirea.web3.web3javaclient.entity.User;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class AccountController {
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private User getUser(Authentication authentication){
        return (User) authentication.getPrincipal();
    }

    private User getUserByToken(String token){
        return restTemplate.getForObject("/user?token=" + token, User.class);
    }

    @GetMapping("/account/{current}")
    public String showAccountPage(Authentication authentication, Model model,
                                  @RequestParam("edit") Optional<String> edit,
                                  @RequestParam("editPicture") Optional<String> editPicture,
                                  @RequestParam("editName") Optional<String> editName,
                                  @RequestParam("editPassword") Optional<String> editPassword,
                                  @RequestParam("editDescription") Optional<String> editDescription,
                                  @RequestParam("editToken") Optional<String> editToken,
                                  @PathVariable String current) {

        if (restTemplate.getForObject("/user?token=" + current, User.class) == null){
            return "redirect:/home?notfoundmsg";
        }

        if (Objects.equals(getUser(authentication).getToken(), current)) {
            model.addAttribute("owner", true);
            if (edit.isPresent() || editName.isPresent() || editPicture.isPresent() || editPassword.isPresent() || editDescription.isPresent() || editToken.isPresent()) {
                model.addAttribute("blur", "5px");
            } else {
                model.addAttribute("blur", "0");
            }
        }
        boolean isSubscribed = getUser(authentication).getSubs().contains(current);
        model.addAttribute("subscribed", isSubscribed);
        model.addAttribute("username", getUserByToken(current).getUsername());
        model.addAttribute("current", current);
        model.addAttribute("myToken", getUser(authentication).getToken());
        model.addAttribute("myUsername", getUser(authentication).getUsername());
        model.addAttribute("image", current + "/image");
        model.addAttribute("description", getUserByToken(current).getDescription());
        List<Post> posts = List.of(Objects.requireNonNull(restTemplate.getForObject("/post?token=" + current, Post[].class)));
        model.addAttribute("posts", posts);
        model.addAttribute("subscriptions", getUserByToken(current).getSubs());
        return "account";
    }

    @GetMapping("/account/{username}/image")
    public void showUserImage(HttpServletResponse response, @PathVariable String username) throws IOException {
        response.setContentType("image/png");
        if (!Objects.equals(username, "0")) {
            User user = restTemplate.getForObject("/user?token=" + username, User.class);

            if (user == null) {
                throw new UsernameNotFoundException("User not found!");
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(user.getProfilePicture());
            IOUtils.copy(bis, response.getOutputStream());
        } else {
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/static/icons/404.png"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bos);
            byte[] data = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            IOUtils.copy(bis, response.getOutputStream());
        }
    }

    @GetMapping("/post/image/{id}")
    public void showPostImage(HttpServletResponse response, @PathVariable String id) throws IOException {
        response.setContentType("image/png");
        Post post = restTemplate.getForObject("/post/" + id, Post.class);
        assert post != null;
        ByteArrayInputStream bis = new ByteArrayInputStream(post.getPicture());
        IOUtils.copy(bis, response.getOutputStream());
    }

    @GetMapping("/subscribe/{token}")
    public String subscribeOnUser(Authentication authentication, @PathVariable String token){
        User user = getUser(authentication);
        if (!Objects.equals(user.getToken(), token)){
            List<String> userSubs = user.getSubs();
            userSubs.add(token);
            user = restTemplate.postForObject("/user?token=" + user.getToken(), user, User.class);
        }
        return "redirect:/account/" + token;
    }


    @GetMapping("/unsubscribe/{token}/{returning}")
    public String unSubscribeOnUser(Authentication authentication,
                                    @PathVariable String token, HttpServletRequest request, @PathVariable Boolean returning){
        User user = getUser(authentication);
        if (!Objects.equals(user.getToken(), token)){
            List<String> userSubs = user.getSubs();
            userSubs.remove(token);
            user = restTemplate.postForObject("/user?token=" + user.getToken(), user, User.class);
        }
        if (returning)
            return "redirect:/account/" + user.getToken();
        else
            return "redirect:/account/" + token;
    }
}
