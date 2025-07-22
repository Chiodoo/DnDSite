package it.uniroma3.siw.controller.logged.giocatore;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatorePersonaggioController {
    
    @Autowired
    private PersonaggioService personaggioService;
    @Autowired
    private CampagnaService campagnaService;


    @GetMapping("campagna/{id}/personaggio")
    public String showGiocatorePersonaggi(@PathVariable Long id, Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna"; // o pagina 404
        }
        Campagna campagna = optionalCampagna.get();
        model.addAttribute("campagna", campagna);
        model.addAttribute("personaggi", personaggioService.findbyCampagna(campagna));
        return "logged/giocatore/giocPersonaggi";
    }

    @GetMapping("/campagna/{id}/personaggio/{personaggioId}")
    public String getPersonaggio(@PathVariable Long id,
                                 @PathVariable Long personaggioId,
                                 Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna"; // o pagina 404
        }
        Campagna campagna = optionalCampagna.get();
        model.addAttribute("campagna", campagna);
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(personaggioId);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/giocatore/campagna/" + id;
        }
        model.addAttribute("personaggio", optionalPersonaggio.get());
        model.addAttribute("campagnaId", id);
        return "logged/giocatore/giocPersonaggio";
    }

    @GetMapping("/campagna/{id}/formNewPersonaggio")
    public String getFormNewPersonaggio(@PathVariable Long id, Model model) {
        model.addAttribute("personaggio", new Personaggio());
        model.addAttribute("campagnaId", id); // utile per collegare il personaggio alla campagna
        return "logged/giocatore/formNewPersonaggio";
    }


    @GetMapping("campagna/{id}/deletePersonaggio/{personaggioId}")
    public String deletePersonaggio(@PathVariable Long id,@PathVariable Long personaggioId, Model model) {
        Optional<Personaggio> personaggioOpt = personaggioService.findById(personaggioId);
        if (!personaggioOpt.isPresent()) {
            model.addAttribute("errorMessage", "Autore non trovato.");
            return "redirect:/logged/giocatore/campagna/"+id+"/personaggio"; // o pagina 404
        }
        Personaggio personaggio = personaggioOpt.get();
        
        if(personaggio.getImage() != null) {
            String imagePath = personaggio.getImage().getPath();
            java.io.File imageFile = new java.io.File("." + imagePath); // Assicurati che il path sia corretto
            if (imageFile.exists()) {
                imageFile.delete(); // Cancella il file immagine fisico
            }
        }
        // Cancella l'autore dal database
        this.personaggioService.deleteById(personaggioId);

        return "redirect:/logged/giocatore/campagna/"+id;
    }


    @PostMapping("/campagna/{id}/formNewPersonaggio")
    public String addPersonaggioToCampagna(@PathVariable Long id,
                                        @ModelAttribute("personaggio") Personaggio personaggio,
                                        @RequestParam("personaggioImage") MultipartFile file,
                                        BindingResult result,
                                        Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("campagnaId", id);
            return "logged/giocatore/campagna/formNewPersonaggio";
        }

        // Recupera la campagna dal database
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            return "redirect:/errore"; // gestisci l'errore come preferisci
        }
        Campagna campagna = optionalCampagna.get();
        // Aggiungi la campagna alla lista del personaggio
        personaggio.getCampagne().add(campagna);

        // Salva il personaggio con l'immagine
        personaggioService.saveWithImage(personaggio, file);

        return "redirect:/logged/giocatore/campagna/" + id ;
    }



    @PostMapping("campagna/{id}/personaggio/{personaggioId}")
    public String updatePersonaggio(@PathVariable Long id,
                            @PathVariable Long personaggioId,
                            @ModelAttribute("author") Personaggio personaggio,
                            @RequestParam(value = "personaggioImage", required = false) MultipartFile file,
                            BindingResult result,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento del personaggio.");
            return "/logged/giocatore/modificaPersonaggio";
        }

        // Opzionale: setta l'ID per sicurezza
        personaggio.setId(personaggioId);
        personaggio.setImage(null); // evita errori di binding su image
        
        // Usa il metodo del servizio per gestire personaggio e immagine
        this.personaggioService.saveWithImage(personaggio, file);
        return "redirect:/logged/giocatore/campagna/"+id;
    }

   

    @GetMapping("/campagna/{id}/modificaPersonaggio/{personaggioId}")
    public String modificaPersonaggio(@PathVariable Long id, @PathVariable Long personaggioId, Model model) {
        Optional<Personaggio> optionalPersonaggio = this.personaggioService.findById(personaggioId);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/giocatore/campagna/"+id;
        }
        model.addAttribute("personaggio", optionalPersonaggio.get());
        model.addAttribute("campagnaId", id); // utile per il form
        return "logged/giocatore/modificaPersonaggio";
    }
    
}
