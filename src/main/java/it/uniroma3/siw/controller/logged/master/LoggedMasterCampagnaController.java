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
import it.uniroma3.siw.model.Nota;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.CampagnaService;
import it.uniroma3.siw.service.NotaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/logged/master")
public class LoggedMasterCampagnaController {

    @Autowired
    private CampagnaService campagnaService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private NotaService notaService;
    
    @GetMapping("/campagna")
    public String showMasterCampagne(Model model, RedirectAttributes redirectAttrs) {
        User user= securityUtils.getCurrentUser();

        if(user==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Qualcosa è andato storto");
            return "redirect:/login";
        }
        Iterable<Campagna> campagneUtente = campagnaService.findCampagneMaster(user.getId());

        model.addAttribute("campagne", campagneUtente);
        return "logged/master/masterCampagne";
    }


    @GetMapping("/campagna/{id}")
    public String getCampagna(@PathVariable Long id, Model model) {
        Optional<Campagna> optionalCampagna = campagnaService.findById(id);
        if (!optionalCampagna.isPresent()) {
            model.addAttribute("errorMessage", "Campagna non trovata.");
            return "redirect:/logged/giocatore/campagna"; // o pagina 404
        }
        Long userCorrenteId = securityUtils.getCurrentUser().getId();
        Campagna campagna = optionalCampagna.get();
        if(userCorrenteId.equals(campagna.getMaster().getId())){
            model.addAttribute("campagna", campagna);
        return "logged/master/masterCampagna";
        }
        return "redirect:/logged/master/campagna";
        
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
    public String addCampagna(@Valid @ModelAttribute("campagna") Campagna campagna, 
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
                                @RequestParam(value="campagnaImage", required =false) MultipartFile imageFile,
                                Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento della campagna.");
            return "redirect:/logged/master/modificaCampagna"; // o pagina 404
        }
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


    @GetMapping("/campagna/{id}/formNewNota")
    public String getFormNewNota(@PathVariable Long id,
                                Model model) {
        Optional<Campagna> optCampagna = campagnaService.findById(id);
        if (optCampagna.isEmpty()) {
            return "redirect:/logged/master/campagna";
        }

        Campagna campagna = optCampagna.get();
        model.addAttribute("campagna", campagna);
        model.addAttribute("nota", new Nota());
        return "logged/master/formNewNota";
    }

    @PostMapping("campagna/{id}/nota")
    public String addNota(@PathVariable Long id,
                            @ModelAttribute("notaDetails") Nota nota,
                            BindingResult result,
                            RedirectAttributes redirectAttrs,
                            Model model) {
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Qualcosa è andato storto durante la creazione della campagna.");
            return "redirect:/logged/master/formNewNota"; // o pagina 404
        }
        Optional<Campagna> optCampagna = campagnaService.findById(id);
        if(!optCampagna.isPresent()){
            model.addAttribute("errorMessage", "Campagna non trovata");
            return "redirect:/logged/master/campagna";
        }
        Campagna campagna = optCampagna.get();
        nota.setId(null);
        nota.setCampagna(campagna);
        notaService.save(nota);
        return "redirect:/logged/master/campagna/" + id;
    }
    
    @PostMapping("campagna/{id}/nota/{notaId}")
    public String updateNota(@PathVariable Long id,
                            @PathVariable Long notaId,
                            @ModelAttribute("nota") Nota nota,
                            BindingResult result,
                            RedirectAttributes redirectAttrs,
                            Model model) {

        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Errore durante aggiornamento.");
            return "redirect:/logged/master/campagna/" + id + "/modificaNota/" + notaId;
        }

        Optional<Nota> optNota = notaService.findById(notaId);
        Optional<Campagna> optCampagna = campagnaService.findById(id);

        if (optNota.isEmpty() || optCampagna.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Nota o Campagna non trovata.");
            return "redirect:/logged/master/campagna/" + id;
        }

        nota.setId(notaId);
        nota.setCampagna(optCampagna.get()); 

        notaService.save(nota);

        return "redirect:/logged/master/campagna/" + id;
    }

    @GetMapping("/campagna/{id}/modificaNota/{notaId}")
    public String getFormModificaNota(@PathVariable Long id,
                                    @PathVariable Long notaId,
                                    Model model) {
        Optional<Campagna> optCampagna = campagnaService.findById(id);
        Optional<Nota> optNota = notaService.findById(notaId);

        if (optCampagna.isEmpty() || optNota.isEmpty()) {
            model.addAttribute("errorMessage", "Campagna o nota non trovata.");
            return "redirect:/logged/master/campagna/" + id;
        }

        Campagna campagna = optCampagna.get();
        Nota nota = optNota.get();

        model.addAttribute("campagna", campagna);
        model.addAttribute("nota", nota);
        return "logged/master/modificaNota"; // ✔️ template giusto
    }

    @GetMapping("/campagna/{campagnaId}/deleteNota/{notaId}")
    public String deleteNota(@PathVariable Long campagnaId,
                            @PathVariable Long notaId,
                            RedirectAttributes redirectAttrs) {
        Optional<Nota> optNota = notaService.findById(notaId);

        if (optNota.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Nota non trovata.");
            return "redirect:/logged/master/campagna/" + campagnaId;
        }

        notaService.deleteById(notaId);
        redirectAttrs.addFlashAttribute("successMessage", "Nota eliminata con successo.");

        return "redirect:/logged/master/campagna/" + campagnaId;
    }

}
