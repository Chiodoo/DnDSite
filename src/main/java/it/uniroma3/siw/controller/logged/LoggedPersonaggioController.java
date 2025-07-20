package it.uniroma3.siw.controller.logged;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.service.PersonaggioService;
import jakarta.validation.Valid;

@Controller
public class LoggedPersonaggioController {

    @Autowired
    private PersonaggioService personaggioService;

    @GetMapping("/personaggio")
    public String showPersonaggi(Model model) {
        model.addAttribute("personaggi", personaggioService.findAll());
        return "personaggi";
    }

    @GetMapping("/personaggio/{id}")
    public String getPersonaggio(@PathVariable Long id, Model model) {
        Optional<Personaggio> optionalPersonaggio = personaggioService.findById(id);
        if (!optionalPersonaggio.isPresent()) {
            return "redirect:/personaggio"; // o una pagina 404
        }
        Personaggio personaggio = optionalPersonaggio.get();
        model.addAttribute("personaggio", personaggio);
        return "personaggio";
    }

    @GetMapping("/formNewPersonaggio")
    public String getFormNewPersonaggio(Model model) {
        model.addAttribute("personaggio", new Personaggio());
        return "formNewPersonaggio";
    }

    @PostMapping("/personaggio")
    public String newAuthor(@Valid @ModelAttribute Personaggio personaggio, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("messaggioErroreTitolo", "Campo obbligatorio");
            return "formNewPersonaggio";
        }else{
            this.personaggioService.save(personaggio);
            model.addAttribute("personaggio", personaggio);
            return "redirect:/personaggio" + personaggio.getId();
        }
    }
    
}