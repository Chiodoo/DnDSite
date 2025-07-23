package it.uniroma3.siw.controller.logged.giocatore;

import java.util.List;
import java.util.Optional;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.model.Nota;
import it.uniroma3.siw.model.Personaggio;
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
            redirectAttrs.addFlashAttribute("errorMessage", "Non puoi partecipare a campagne senza personaggi. Prima crea il tuo personaggio.");
            return "redirect:/logged/giocatore/personaggio/new";
        }

        // Campagne totali
        model.addAttribute("campagne", campagnaService.findAll());

        // Campagne a cui partecipa il giocatore
        Set<Campagna> campagnePartecipate = campagnaService.findCampagneByUserId(userId);
        model.addAttribute("campagnePartecipate", campagnePartecipate);

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
        return "logged/giocatore/giocCampagna";
    }
    
   @GetMapping("/partecipaCampagna/{id}")
    public String partecipaCampagna(@PathVariable Long id, RedirectAttributes redirectAttrs, Model model) {
        Long userId = securityUtils.getCurrentUser().getId();

        // 1. Controllo: ha personaggi?
        List<Personaggio> personaggi = personaggioService.findByUserId(userId);
        if (personaggi.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Non puoi partecipare a campagne senza personaggi. Prima crea il tuo personaggio.");
            return "redirect:/logged/giocatore/personaggio/new";
        }

        // 2. Recupera campagna
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (optionalCampagna.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna";
        }

        Campagna campagna = optionalCampagna.get();

        // 3. Controllo: partecipa già?
        Set<Campagna> campagnePartecipate = campagnaService.findCampagneByUserId(userId);
        if (campagnePartecipate.contains(campagna)) {
            redirectAttrs.addFlashAttribute("infoMessage", "Hai già aderito a questa campagna.");
            return "redirect:/logged/giocatore/campagna";
        }
        Long userCorrentId = securityUtils.getCurrentUser().getId();
        if(userCorrentId.equals(campagna.getMaster().getId())){
            redirectAttrs.addFlashAttribute("infoMessage", "Non puoi partecipare a una campagna che hai creato");
            return "redirect:/logged/giocatore/campagna";
        }

        // 4. Mostra il form
        model.addAttribute("campagna", campagna);
        model.addAttribute("personaggi", personaggi);
        return "logged/giocatore/partecipaForm";
    }


    @PostMapping("/partecipaCampagna/{campagnaId}")
    public String processPartecipaCampagna(@PathVariable Long campagnaId,
                                           @RequestParam Long personaggioId,
                                           RedirectAttributes redirectAttrs) {
        try {
            campagnaService.addPartecipante(campagnaId, personaggioId);
            redirectAttrs.addFlashAttribute("successMessage",
                "Iscrizione alla campagna avvenuta con successo!");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("errorMessage",
                "Si è verificato un errore. Riprova più tardi.");
        }
        return "redirect:/";
    }

    @GetMapping("/mieCampagne")
    public String showCampagnePartecipate(Model model, RedirectAttributes redirectAttrs) {

        Long userId = securityUtils.getCurrentUser().getId();
        if (personaggioService.findByUserId(userId).isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Non puoi avere campagne senza personaggi. Prima crea il tuo personaggio.");
            return "redirect:/logged/giocatore/personaggio/new";
        }

        Set<Campagna> campagne = campagnaService.findCampagneByUserId(userId);
        model.addAttribute("campagnePartecipate", campagne);
        return "logged/giocatore/giocCampagnePartecipate";
    }
    
    

    
    
   


}
