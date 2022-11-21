package ru.mirea.web3.web3javaclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EditController {
    @GetMapping("/account-edit")
    public String showEditPage(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "edit";
    }
}
