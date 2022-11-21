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
import ru.mirea.web3.web3javaclient.entity.User;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Controller
public class AccountController {
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/account/{current}")
    public String showAccountPage(Authentication authentication, Model model,
                                  @RequestParam("edit") Optional<String> edit,
                                  @PathVariable String current) {

        if (Objects.equals(authentication.getName(), current)) {
            model.addAttribute("owner", true);
            if (edit.isPresent()) {
                model.addAttribute("blur", "5px");
            } else {
                model.addAttribute("blur", "0");
            }
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("current", current);
        model.addAttribute("image", current + "/image");

        return "account";
    }

    @GetMapping("/account/{username}/image")
    public void showUserImage(HttpServletResponse response, @PathVariable String username) throws IOException {
        response.setContentType("image/png");
        if (!Objects.equals(username, "0")) {
            User user = restTemplate.getForObject("/user?username=" + username, User.class);

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
}
