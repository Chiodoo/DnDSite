package it.uniroma3.siw.controller.logged.giocatore;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreController {



    @GetMapping("/giocatoreIndex")
    public String getHomePage() {
        return "/logged/giocatore/giocatoreIndex";
    }
    
}
