package ru.mirea.web3.web3javaclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mirea.web3.web3javaclient.entity.User;

@Controller
public class ErrorController {
    @ExceptionHandler(Exception.class)
    public String showErrorPage(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("username", user.getUsername());
        return "error";
    }
}
