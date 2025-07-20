package it.uniroma3.siw.controller.logged.giocatore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Personaggio;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreController {



    @GetMapping("/giocatoreIndex")
    public String getHomePage() {
        return "/logged/giocatore/giocatoreIndex";
    }

    @GetMapping("/formNewPersonaggio")
    public String createNewPersonaggio(Model model) {
        model.addAttribute("personaggio", new Personaggio());
        return "/logged/giocatore/formNewPersonaggio";
    }

}
