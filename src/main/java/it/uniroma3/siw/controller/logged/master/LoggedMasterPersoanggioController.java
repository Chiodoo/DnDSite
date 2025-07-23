package it.uniroma3.siw.controller.logged.master;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.service.*;
import it.uniroma3.siw.model.*;


@Controller
@RequestMapping("/logged/master")
public class LoggedMasterPersoanggioController {

    @Autowired
    private PersonaggioService personaggioService;


    @GetMapping("/personaggio")
    public String showMasterPersonaggi(Model model) {
        model.addAttribute("personaggi", personaggioService.findAll());
        return "logged/master/masterPersonaggi";
    }

    @GetMapping("/personaggio/{id}")
    public String getPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/master/personaggio"; // o pagina 404
        }
        Personaggio personaggio = optionalPersonaggio.get();
        model.addAttribute("personaggio", personaggio);
        return "logged/master/masterPersonaggio";
    }

    @GetMapping("/campagna/{id}/personaggio/{personaggioId}")
    public String getPersonaggioDaCampagna(@PathVariable Long id,
                                             @PathVariable Long personaggioId,
                                              Model model,
                                              RedirectAttributes redirectAttrs) {
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(personaggioId);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/master/campagna/" + id; // o pagina 404
        }
        Personaggio personaggio = optionalPersonaggio.get();
        model.addAttribute("personaggio", personaggio);
        model.addAttribute("campagnaId", id);
        return "logged/master/masterPersonaggio";
    }
    
}
