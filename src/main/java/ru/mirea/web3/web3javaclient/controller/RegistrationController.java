package ru.mirea.web3.web3javaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.User;
import ru.mirea.web3.web3javaclient.security.SecurityConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RegistrationController {
    private RestTemplate restTemplate;
    private SecurityConfiguration securityConfiguration;

    @Autowired
    public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/registration")
    public String showRegistrationForm(HttpServletRequest request, Model model) {
        if (request.getUserPrincipal() != null) {
            model.addAttribute("onload", "exit(0)");
        }
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String completeRegistration(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "registration";
        }

        if (user.getRepeated().isEmpty()) {
            model.addAttribute("reperr", "Repeated password can not be empty!");
            return "registration";
        }

        if (!user.getPassword().equals(user.getRepeated())) {
            model.addAttribute("reperr", "Passwords do not match!");
            return "registration";
        }

        if (restTemplate.getForObject("/user?username=" + user.getUsername(), User.class) == null) {
            user.setPassword(securityConfiguration.encoder().encode(user.getPassword()));
            restTemplate.postForObject("/user", user, User.class);
            model.addAttribute("onload", "exit(1)");
        } else {
            bindingResult.rejectValue("username", "user.username", "This username is already taken!");
        }
        return "registration";
    }
}
