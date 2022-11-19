package ru.mirea.web3.web3javaclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class ErrorController {
    @ExceptionHandler(Exception.class)
    public String showErrorPage() {
        return "error";
    }
}
