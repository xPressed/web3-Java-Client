package ru.mirea.web3.web3javaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.User;
import ru.mirea.web3.web3javaclient.security.SecurityConfiguration;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    public String showRegistrationForm(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("onload", "exit(0)");
        }
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping(value = "/registration")
    @ResponseBody
    public String completeRegistration(@Valid User user, BindingResult bindingResult, Model model, HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "<br>Error with creds. Check your data</h2>"
                    + "<h3><a href='" + request.getLocalAddr() + "'>To main page</a><h3>";
        }
        if (user.getRepeated().isEmpty()) {
            return "<br>Error with creds. Repeated password can not be empty!</h2>"
                    + "<h3><a href='" + request.getLocalAddr() + "'>To main page</a><h3>";
        }

        if (!user.getPassword().equals(user.getRepeated())) {
            return "<br>Error with creds. Your passwords do not match!</h2>"
                    + "<h3><a href='" + request.getLocalAddr() + "'>To main page</a><h3>";
        }

        String pass = user.getPassword();
        user.setPassword(securityConfiguration.encoder().encode(user.getPassword()));

        BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/static/icons/defaultPicture.png"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bos);
        byte[] data = bos.toByteArray();
        user.setProfilePicture(data);

        user = restTemplate.postForObject("/user", user, User.class);
        model.addAttribute("onload", "exit(1)");

        //Add token text

        return "<h2>TOKEN: " + user.getToken() + "<br>Name: " + user.getUsername() + "<br>Password: " + pass
                + "<br>Remember the data, in case of loss of the token, you will not be able to access the account</h2>"
                + "<h3><a href='" + request.getLocalAddr() + "'>To main page</a><h3>";
    }
}
