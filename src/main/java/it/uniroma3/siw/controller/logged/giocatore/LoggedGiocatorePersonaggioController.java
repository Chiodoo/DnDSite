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
@RequestMapping("/giocatore")
public class LoggedGiocatorePersonaggioController {
    
    @Autowired
    private PersonaggioService personaggioService;


    @GetMapping("/personaggio")
    public String showAdminAuthors(Model model) {
        model.addAttribute("personaggi", personaggioService.findAll());
        return "giocatore/giocPersonaggi";
    }

    @GetMapping("/personaggio/{id}")
    public String getPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/giocatore/personaggio"; // o pagina 404
        }
        Personaggio personaggio = optionalPersonaggio.get();
        model.addAttribute("personaggio", personaggio);
        return "giocatore/adminPersonaggio";
    }

    @GetMapping("/formNewPersonaggio")
    public String getFormNewPersonaggio(Model model) {
        model.addAttribute("personaggio", new Personaggio());
        return "giocatore/formNewPersonaggio";
    }

    @GetMapping("/deletePersonaggio/{id}")
    public String deletePersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> personaggioOpt = personaggioService.findById(id);
        if (!personaggioOpt.isPresent()) {
            model.addAttribute("errorMessage", "Autore non trovato.");
            return "redirect:/giocatore/personaggio"; // o pagina 404
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

        return "redirect:/giocatore/personaggio";
    }


    @PostMapping("/personaggio")
    public String addPersonaggio(@ModelAttribute("personaggio") Personaggio personaggio,
                            @RequestParam("personaggioImages") MultipartFile file,
                            BindingResult result,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            return "giocatore/formNewPersonaggio";
        }

        // Usa il servizio per salvare personaggio e immagine
        this.personaggioService.saveWithImage(personaggio, file);

        return "redirect:/giocatore/personaggio";
    }


    @PostMapping("/personaggio/{id}")
    public String updateAuthor(@PathVariable Long id,
                            @ModelAttribute("author") Personaggio personaggio,
                            @RequestParam(value = "authorImages", required = false) MultipartFile file,
                            BindingResult result,
                            Model model) throws IOException {
        if (result.hasErrors()) {
            return "giocatore/modificaPersonaggio";
        }

        // Opzionale: setta l'ID per sicurezza
        personaggio.setId(id);
        personaggio.setImage(null); // evita errori di binding su image

        // Usa il metodo del servizio per gestire personaggio e immagine
        this.personaggioService.saveWithImage(personaggio, file);
        return "redirect:/giocatore/personaggio";
    }

   

    @GetMapping("/modificaPersonaggio/{id}")
    public String modificaPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = this.personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            model.addAttribute("errorMessage", "Personaggio non trovato.");
            return "redirect:/giocatore/personaggio";
        }
        model.addAttribute("personaggio", optionalPersonaggio.get());
        return "giocatore/modificaPersonaggio";
    }
    
}
