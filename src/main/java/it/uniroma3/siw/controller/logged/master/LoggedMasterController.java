package it.uniroma3.siw.controller.logged.master;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Campagna;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logged/master")
public class LoggedMasterController {



    @GetMapping("/masterIndex")
    public String getHomePage() {
        return "/logged/master/masterIndex";
    }

    @GetMapping("/formNewCampagna")
    public String createNewCampagna(Model model) {
        model.addAttribute("campagna", new Campagna());
        return "/logged/master/formNewCampagna";
    }
    
}
