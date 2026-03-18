package com.outsera.razzies.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirecionarParaSwagger() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/favicon.ico")
    public String redirecionarFavicon() {
        return "redirect:/swagger-ui.html";
    }
}
