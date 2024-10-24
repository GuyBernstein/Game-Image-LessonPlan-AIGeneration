package com.handson.sentiment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Home page redirect
    @GetMapping("/")
    public String home() {
        return "redirect:/landing.html";
    }
}