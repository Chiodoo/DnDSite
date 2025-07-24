package it.uniroma3.siw.controller.logged.giocatore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.PersonaggioService;

@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatorePersonaggioController {

    @Autowired
    private PersonaggioService personaggioService;

    @Autowired
    private SecurityUtils securityUtils;


    @GetMapping("/personaggi")
    public String showUserPersonaggi(Model model) {
        User user = securityUtils.getCurrentUser();
        List<Personaggio> personaggi = personaggioService.findByUserId(user.getId());
        model.addAttribute("personaggi", personaggi);
        return "logged/giocatore/giocPersonaggi";
    }

    
    @GetMapping("/personaggio/new")
    public String getFormNewPersonaggio(Model model,
                                        @ModelAttribute("errorMessage") String errorMessage) {
        model.addAttribute("personaggio", new Personaggio());
        // il template leggerà l’eventuale flash errorMessage
        return "logged/giocatore/formNewPersonaggio";
    }

    
    @PostMapping("/personaggio/new")
    public String addPersonaggio(@ModelAttribute("personaggio") Personaggio personaggio,
                                 @RequestParam("personaggioImage") MultipartFile file,
                                 BindingResult result,
                                 RedirectAttributes redirectAttrs) throws IOException {
        if (result.hasErrors()) {
            return "logged/giocatore/formNewPersonaggio";
        }
        User user = securityUtils.getCurrentUser();
        personaggio.setUser(user);
        personaggioService.saveWithImage(personaggio, file);
        return "redirect:/logged/giocatore/personaggi";
    }

    
    @GetMapping("/personaggio/{id}")
    public String viewPersonaggio(@PathVariable Long id,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {
        Optional<Personaggio> opt = personaggioService.findById(id);
        if (!opt.isPresent() || !opt.get().getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Personaggio non trovato o non autorizzato.");
            return "redirect:/logged/giocatore/personaggi";
        }
        model.addAttribute("personaggio", opt.get());
        return "logged/giocatore/giocPersonaggio";
    }

    @GetMapping("/campagna/{id}/personaggio/{personaggioId}")
    public String showPersonaggioDaCampagna(@PathVariable Long id,
                                            @PathVariable Long personaggioId,
                                            Model model,
                                            RedirectAttributes redirectAttrs) {
        Optional<Personaggio> opt = personaggioService.findById(personaggioId);
        if (!opt.isPresent() || !opt.get().getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Personaggio non trovato o non autorizzato.");
            return "redirect:/logged/giocatore/campagna/" + id;
        }
        model.addAttribute("personaggio", opt.get());
        model.addAttribute("campagnaId", id);
        return "logged/giocatore/giocPersonaggio";
    }
    
    
    @GetMapping("/personaggio/{id}/edit")
    public String editPersonaggio(@PathVariable Long id,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {
        Optional<Personaggio> opt = personaggioService.findById(id);
        if (!opt.isPresent() || !opt.get().getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Personaggio non trovato o non autorizzato.");
            return "redirect:/logged/giocatore/personaggi";
        }
        model.addAttribute("personaggio", opt.get());
        return "logged/giocatore/modificaPersonaggio";
    }

    
    @PostMapping("/personaggio/{id}/edit")
    public String updatePersonaggio(@PathVariable Long id,
                                    @ModelAttribute("personaggio") Personaggio personaggio,
                                    @RequestParam(value = "personaggioImage", required = false) MultipartFile file,
                                    BindingResult result,
                                    RedirectAttributes redirectAttrs) throws IOException {
        if (result.hasErrors()) {
            return "logged/giocatore/modificaPersonaggio";
        }
        Optional<Personaggio> opt = personaggioService.findById(id);
        if (!opt.isPresent() || !opt.get().getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Personaggio non trovato o non autorizzato.");
            return "redirect:/logged/giocatore/personaggi";
        }
        Personaggio existing = opt.get();
        // aggiorno i campi
        existing.setNome(personaggio.getNome());
        existing.setRazza(personaggio.getRazza());
        existing.setClasse(personaggio.getClasse());
        existing.setLivello(personaggio.getLivello());
        existing.setDescrizione(personaggio.getDescrizione());
        personaggioService.saveWithImage(existing, file);
        return "redirect:/logged/giocatore/personaggi";
    }

    
    @GetMapping("/personaggio/{id}/delete")
    public String deletePersonaggio(@PathVariable Long id,
                                    RedirectAttributes redirectAttrs) {
        Optional<Personaggio> opt = personaggioService.findById(id);
        if (!opt.isPresent() || !opt.get().getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Personaggio non trovato o non autorizzato.");
            return "redirect:/logged/giocatore/personaggi";
        }
        Personaggio personaggio = opt.get();
        if (personaggio.getImage() != null) {
            String imagePath = personaggio.getImage().getPath();
            File imageFile = new File("." + imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
        personaggioService.deleteById(id);
        return "redirect:/logged/giocatore/personaggi";
    }
}
