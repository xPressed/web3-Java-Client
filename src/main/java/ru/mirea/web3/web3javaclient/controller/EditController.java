package ru.mirea.web3.web3javaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.web3.web3javaclient.entity.User;
import ru.mirea.web3.web3javaclient.security.SecurityConfiguration;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

@Controller
public class EditController {

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

    private String getToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return user.getToken();
    }

    @GetMapping(value = {"/account-edit",
            "/account-editPicture",
            "/account-editName",
            "/account-editPassword",
            "account-editDescription",
            "account-editToken"})
    public String showEditPage(Authentication authentication, Model model, HttpServletRequest request) {
        model.addAttribute("username", getToken(authentication));
        String[] templateOutList = request.getServletPath().split("-");
        return templateOutList[templateOutList.length - 1];
    }

    @PostMapping("/account-editPicture")
    public String editPicturePage(Authentication authentication, Model model,
                                      @RequestParam("picture") MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            model.addAttribute("error.param", true);
            return "editPicture";
        }
        User user = (User) authentication.getPrincipal();
        user.setProfilePicture(data);
        restTemplate.postForObject("/user", user, User.class);
        model.addAttribute("onload", "parent.closeIFrame('" + user.getToken() + "')");
        return "editPicture";
    }

    @PostMapping("/account-editName")
    public String editNamePage(Authentication authentication, Model model,
                                  @RequestParam("username") String name) {
        User user = (User) authentication.getPrincipal();
        user.setUsername(name);
        restTemplate.postForObject("/user", user, User.class);
        model.addAttribute("onload", "parent.closeIFrame('" + user.getToken() + "')");
        return "editName";
    }

    @PostMapping("/account-editDescription")
    public String editDescriptionPage(Authentication authentication, Model model,
                               @RequestParam("description") String description) {

        User user = (User) authentication.getPrincipal();
        user.setDescription(description);
        restTemplate.postForObject("/user", user, User.class);
        model.addAttribute("onload", "parent.closeIFrame('" + user.getToken() + "')");
        return "editDescription";
    }

    @PostMapping("/account-editPassword")
    public String editPasswordPage(Authentication authentication, Model model,
                                   @RequestParam("password") String password, @RequestParam("repeat") String repeat) {

        if (repeat.isEmpty()) {
            model.addAttribute("reperr", "Repeated password can not be empty!");
            return "editPassword";
        }

        if (!password.equals(repeat)) {
            model.addAttribute("reperr", "Passwords do not match!");
            return "editPassword";
        }

        User user = (User) authentication.getPrincipal();
        user.setPassword(password);
        user.setRepeated(repeat);
        user.setPassword(securityConfiguration.encoder().encode(user.getPassword()));
        restTemplate.postForObject("/user", user, User.class);
        model.addAttribute("onload", "parent.closeIFrame('" + user.getToken() + "')");
        return "editPassword";
    }

    @PostMapping("/account-editToken")
    @ResponseBody
    public String editTokenPage(Authentication authentication, Model model, HttpServletRequest request) {
        User user = (User) authentication.getPrincipal();
        user = restTemplate.getForObject("/user/rebuild?token=" + user.getToken(), User.class);
        model.addAttribute("onload", "parent.closeIFrame('" + user.getToken() + "')");
        return "<h2>TOKEN: " + user.getToken() + "<br>Name: " + user.getUsername()
                + "<br>Remember the data, in case of loss of the token, you will not be able to access the account</h2>"
                + "<h3><a href='/logout'>To main page</a><h3>";
    }
}
