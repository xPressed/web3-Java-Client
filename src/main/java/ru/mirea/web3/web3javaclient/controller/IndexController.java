package ru.mirea.web3.web3javaclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mirea.web3.web3javaclient.entity.User;

import java.util.Optional;

@Controller
public class IndexController {

    private String getToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return user.getToken();
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
            model.addAttribute("username", authentication.getName());
            model.addAttribute("linkOutOrUp", "/logout");
            model.addAttribute("textOutOrUp", "Log Out");
            model.addAttribute("linkInOrAccount", "/account/" + getToken(authentication));
            model.addAttribute("textInOrAccount", "Account");
            model.addAttribute("newpost", true);
            model.addAttribute("latest", true);
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
}
