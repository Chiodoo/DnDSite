package it.uniroma3.siw.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import it.uniroma3.siw.service.PersonaggioService;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class PersonaggioController {

    @Autowired PersonaggioService personaggioService;

    @GetMapping("/character/{id}")
    public String getPersonaggio(@RequestParam Long id, Model model) {
        model.addAttribute("personaggio", this.personaggioService.getPersonaggioById(id));
        return "characterPage.html";
    }

    
    
    @GetMapping("/character")
    public String mostraPersonaggi(Model model) {
        model.addAttribute("personaggi", this.personaggioService.getAllPersonaggi());
        return "characters.html";
    }
    
    
}
