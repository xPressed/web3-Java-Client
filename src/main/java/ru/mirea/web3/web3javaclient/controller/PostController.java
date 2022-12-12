package ru.mirea.web3.web3javaclient.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.web3.web3javaclient.entity.Post;
import ru.mirea.web3.web3javaclient.entity.User;
import java.io.IOException;
import java.util.Date;

@Controller
public class PostController {

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return user.getToken();
    }

    @GetMapping("/new")
    public String getCreateTemplate(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("linkInOrAccount", "/account/" + getToken(authentication));
        return "create";
    }

    @PostMapping("/new")
    public String postCreate(Model model, Authentication authentication,
                             @RequestParam("picture")MultipartFile file,
                             @RequestParam("description") String description) throws IOException {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("linkInOrAccount", "/account/" + getToken(authentication));
        if (description.length() < 3 || description.length() > 50){
            model.addAttribute("badcred", true);
            return "create";
        }
        Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setPicture(file.getBytes());
        newPost.setUserToken(getToken(authentication));
        newPost.setDate(new Date());
        restTemplate.postForObject("/post", newPost, Post.class);
        model.addAttribute("success", true);
        return "create";
    }
}
