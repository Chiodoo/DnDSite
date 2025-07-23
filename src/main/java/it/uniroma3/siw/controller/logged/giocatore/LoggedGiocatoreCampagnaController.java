package it.uniroma3.siw.controller.logged.giocatore;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.CampagnaService;
import it.uniroma3.siw.service.PersonaggioService;

@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreCampagnaController {

    @Autowired
    private CampagnaService campagnaService;

    @Autowired
    private PersonaggioService personaggioService;

    @Autowired
    private SecurityUtils securityUtils;
    
    @GetMapping("/campagna")
    public String showGiocatoreCampagne(Model model, RedirectAttributes redirectAttrs){
        Long userId = securityUtils.getCurrentUser().getId();
        if (personaggioService.findByUserId(userId).isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Non hai personaggi associati. Prima crea il tuo personaggio.");
            return "redirect:/logged/giocatore/personaggio/new";
        }
        model.addAttribute("campagne", campagnaService.findAll());
        return "logged/giocatore/giocCampagne";
    }

    @GetMapping("/campagna/{id}")
    public String getCampagna(@PathVariable Long id, Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna";
        }
        model.addAttribute("campagna", optionalCampagna.get());
        return "logged/giocatore/giocCampagne";
    }
}
