package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class webController {

    @GetMapping("/")
    public String getHomePage() {
        return "index.html";
    }
    
    
}
