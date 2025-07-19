package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.CredentialsService;

@Controller
@RequestMapping("/logged")
public class LoggedRedirectionController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/diventaGiocatore")
    public String redirectGiocatore() {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        this.credentialsService.changeToGiocatore(user.getId());

        return "redirect:/logged/giocatore/giocatoreIndex";
    }

    @GetMapping("/diventaMaster")
    public String redirectMaster() {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        this.credentialsService.changeToMaster(user.getId());

        return "redirect:/logged/master/masterIndex";
    }
}
