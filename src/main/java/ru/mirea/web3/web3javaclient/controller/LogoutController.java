package ru.mirea.web3.web3javaclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String executeLogout(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("update") Optional<String> update,
                                @RequestParam("delete") Optional<String> delete) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        if (update.isPresent()) {
            return "redirect:/index?updatemsg";
        }

        if (delete.isPresent()) {
            return "redirect:/index?deletemsg";
        }

        return "redirect:/index?logoutmsg";
    }
}
