package ru.mirea.web3.web3javaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.Post;
import ru.mirea.web3.web3javaclient.entity.User;
import ru.mirea.web3.web3javaclient.security.SecurityConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class IndexController {

    private RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return user.getToken();
    }

    private List<String> getSubs(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return user.getSubs();
    }

    private Model AuthenticationPreset(Model model, Authentication authentication){
        model.addAttribute("username", authentication.getName());
        model.addAttribute("linkOutOrUp", "/logout");
        model.addAttribute("textOutOrUp", "Log Out");
        model.addAttribute("linkInOrAccount", "/account/" + getToken(authentication));
        model.addAttribute("textInOrAccount", "Account");
        model.addAttribute("newpost", true);
        model.addAttribute("latest", true);
        return model;
    }


    @GetMapping({"/index", "/", "/home"})
    public String showIndexPage(Authentication authentication, Model model,
                                @RequestParam("login") Optional<String> login,
                                @RequestParam("registration") Optional<String> registration) {
        if (login.isPresent() | registration.isPresent()) {
            model.addAttribute("overflow", "hidden");
            model.addAttribute("blur", "5px");
        } else {
            model.addAttribute("overflow", "visible");
            model.addAttribute("blur", "0");
        }

        if (authentication != null) {
            AuthenticationPreset(model, authentication);
            model.addAttribute("subscriptions", getSubs(authentication));
            model.addAttribute("count", restTemplate.getForObject("/countPosts?token=" + getToken(authentication), Long.class));
        } else {
            model.addAttribute("username", "Stranger?");
            model.addAttribute("linkOutOrUp", "/index?registration");
            model.addAttribute("textOutOrUp", "Sign Up");
            model.addAttribute("linkInOrAccount", "/index?login");
            model.addAttribute("textInOrAccount", "Log In");
            model.addAttribute("newpost", false);
            model.addAttribute("latest", false);
        }

        return "index";
    }

    @GetMapping("/home/{token}/{depth}")
    public String showSubsPosts(Authentication authentication, Model model,
                                @PathVariable String depth, @PathVariable String token){
        AuthenticationPreset(model, authentication);
        model.addAttribute("count", restTemplate.getForObject("/countSubs?token=" + token, Long.class));
        if (depth != null){
            model.addAttribute("depth", Integer.parseInt(depth));
            model.addAttribute("tokenOwner", token);
            List<Post> posts = List.of(Objects.requireNonNull(restTemplate.getForObject("/postDepth?token=" + token + "&depth=" + depth, Post[].class)));
            model.addAttribute("posts", posts);
        }
        return "index";
    }
}
