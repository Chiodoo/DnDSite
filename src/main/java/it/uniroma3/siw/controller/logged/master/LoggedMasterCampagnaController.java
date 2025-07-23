package it.uniroma3.siw.controller.logged.master;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.service.CampagnaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/logged/master")
public class LoggedMasterCampagnaController {

    @Autowired
    private CampagnaService campagnaService;
    
    @GetMapping("/campagna")
    public String showMasterCampagne(Model model){
        model.addAttribute("campagne", campagnaService.findAll());
        return "logged/master/masterCampagne";
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
        return "logged/master/masterCampagna";
    }

    @GetMapping("/formNewCampagna")
    public String getFormNewCampagna(Model model) {
        model.addAttribute("campagna", new Campagna());
        return "logged/master/formNewCampagna";
    }

    @GetMapping("/deleteCampagna/{id}")
    public String deleteCampagna(@PathVariable Long id, Model model) {
        Optional<Campagna> campagnaOpt = campagnaService.findById(id);
        if (!campagnaOpt.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/master/campagna"; // o pagina 404
        }
        campagnaService.deleteById(id);
        return "redirect:/logged/master/campagna";
    }
    @PostMapping("/campagna")
    public String addCampagna(@Valid@ModelAttribute("campagna") Campagna campagna, 
                                BindingResult result,
                                @RequestParam(value="campagnaImage", required=false) MultipartFile imageFile,
                                Model model, RedirectAttributes redirectAttrs) throws IOException {
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Qualcosa è andato storto durante la creazione della campagna.");
            return "redirect:/logged/master/formNewCampagna"; // o pagina 404
        }
        campagnaService.saveWithImage(campagna, imageFile);
        return "redirect:/logged/master/campagna";
    }

    @PostMapping("/campagna/{id}")
    public String updateCampagna(@PathVariable Long id, 
                                @ModelAttribute("campagna") Campagna campagna, 
                                BindingResult result,
                                @RequestParam(value="image", required =false) MultipartFile imageFile,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento della campagna.");
            return "redirect:/logged/master/modificaCampagna"; // o pagina 404
        }
        campagna.setId(id);
        campagna.setImage(null);

        this.campagnaService.saveWithImage(campagna, imageFile);
        return "redirect:/logged/master/campagna";
    }

    @GetMapping("/modificaCampagna/{id}")
    public String getFormModificaCampagna(@PathVariable Long id, Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/master/campagna"; // o pagina 404
        }
        Campagna campagna = optionalCampagna.get();
        model.addAttribute("campagna", campagna);
        return "logged/master/modificaCampagna";
    }
}
