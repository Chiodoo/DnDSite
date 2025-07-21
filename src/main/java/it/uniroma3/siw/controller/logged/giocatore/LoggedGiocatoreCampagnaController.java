package it.uniroma3.siw.controller.logged.giocatore;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.service.CampagnaService;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreCampagnaController {

    @Autowired
    private CampagnaService campagnaService;
    
    @GetMapping("/campagna")
    public String showGiocatoreCampagne(Model model){
        model.addAttribute("campagne", campagnaService.findAll());
        return "logged/giocatore/giocCampagne";
    }

    @GetMapping("/campagna/{id}")
    public String getCampagna(@PathVariable Long id, Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna"; // o pagina 404
        }
        Campagna campagna = optionalCampagna.get();
        model.addAttribute("campagna", campagna);
        return "logged/giocatore/giocCampagna";
    }


    
}
