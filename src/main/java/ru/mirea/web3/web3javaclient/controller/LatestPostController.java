package ru.mirea.web3.web3javaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.Post;
import ru.mirea.web3.web3javaclient.entity.User;

import java.util.List;
import java.util.Objects;

@Controller
public class LatestPostController {

    private User getUser(Authentication authentication){
        return (User) authentication.getPrincipal();
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/latest/{depth}")
    public String viewLatestPosts(Model model, Authentication authentication, @PathVariable String depth){
        model.addAttribute("depth", Integer.parseInt(depth));
        model.addAttribute("myToken", getUser(authentication).getToken());
        model.addAttribute("myUsername", getUser(authentication).getUsername());
        List<Post> posts = List.of(Objects.requireNonNull(restTemplate.getForObject("/posts?depth=" + depth, Post[].class)));
        model.addAttribute("count", restTemplate.getForObject("/countPosts", Long.class));
        model.addAttribute("posts", posts);
        return "latest";
    }
}
