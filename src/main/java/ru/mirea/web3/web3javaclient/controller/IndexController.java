package ru.mirea.web3.web3javaclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class IndexController {
    @GetMapping({"/index", "/", "/home"})
    public String showIndexPage(HttpServletRequest request, Model model,
                                @RequestParam("login") Optional<String> login,
                                @RequestParam("registration") Optional<String> registration,
                                @RequestParam("account") Optional<String> account) {
        if (login.isPresent() | registration.isPresent() | account.isPresent()) {
            model.addAttribute("blur", "5px");
        } else {
            model.addAttribute("blur", "0");
        }

        if (request.getUserPrincipal() != null) {
            model.addAttribute("username", request.getUserPrincipal().getName());
            model.addAttribute("linkOutOrUp", "/logout");
            model.addAttribute("textOutOrUp", "Log Out");
            model.addAttribute("linkInOrAccount", "/index?account");
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
