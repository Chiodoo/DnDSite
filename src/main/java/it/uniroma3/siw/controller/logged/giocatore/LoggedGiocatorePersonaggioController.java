package it.uniroma3.siw.controller.logged.giocatore;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.service.PersonaggioService;
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


    @GetMapping("/personaggio")
    public String showGiocatorePersonaggi(Model model) {
        model.addAttribute("personaggi", personaggioService.findAll());
        return "logged/giocatore/giocPersonaggi";
    }

    @GetMapping("/personaggio/{id}")
    public String getPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/giocatore/personaggio"; // o pagina 404
        }
        Personaggio personaggio = optionalPersonaggio.get();
        model.addAttribute("personaggio", personaggio);
        return "logged/giocatore/giocPersonaggio";
    }

    @GetMapping("/formNewPersonaggio")
    public String getFormNewPersonaggio(Model model) {
        model.addAttribute("personaggio", new Personaggio());
        return "logged/giocatore/formNewPersonaggio";
    }

    @GetMapping("/deletePersonaggio/{id}")
    public String deletePersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> personaggioOpt = personaggioService.findById(id);
        if (!personaggioOpt.isPresent()) {
            model.addAttribute("errorMessage", "Autore non trovato.");
            return "redirect:/logged/giocatore/personaggio"; // o pagina 404
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
        this.personaggioService.deleteById(id);

        return "redirect:/logged/giocatore/personaggio";
    }


    @PostMapping("/personaggio")
    public String addPersonaggio(@ModelAttribute("personaggio") Personaggio personaggio,
                            @RequestParam("personaggioImage") MultipartFile file,
                            BindingResult result,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            return "logged/giocatore/formNewPersonaggio";
        }

        // Usa il servizio per salvare personaggio e immagine
        this.personaggioService.saveWithImage(personaggio, file);

        return "redirect:/logged/giocatore/personaggio";
    }


    @PostMapping("/personaggio/{id}")
    public String updatePersonaggio(@PathVariable Long id,
                            @ModelAttribute("author") Personaggio personaggio,
                            @RequestParam(value = "personaggioImage", required = false) MultipartFile file,
                            BindingResult result,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento del personaggio.");
            return "/logged/giocatore/modificaPersonaggio";
        }

        // Opzionale: setta l'ID per sicurezza
        personaggio.setId(id);
        personaggio.setImage(null); // evita errori di binding su image

        // Usa il metodo del servizio per gestire personaggio e immagine
        this.personaggioService.saveWithImage(personaggio, file);
        return "redirect:/logged/giocatore/personaggio";
    }

   

    @GetMapping("/modificaPersonaggio/{id}")
    public String modificaPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = this.personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/logged/giocatore/personaggio";
        }
        model.addAttribute("personaggio", optionalPersonaggio.get());
        return "logged/giocatore/modificaPersonaggio";
    }
    
}
