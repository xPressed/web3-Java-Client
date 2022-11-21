package ru.mirea.web3.web3javaclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String showLoginPage(Authentication authentication, Model model, @RequestParam("complete") Optional<String> complete) {
        if (complete.isPresent() || authentication != null) {
            model.addAttribute("onload", "exit()");
        }
        return "login";
    }
}
