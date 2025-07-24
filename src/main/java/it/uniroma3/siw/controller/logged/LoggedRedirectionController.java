package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.AuthenticationRefreshService;
import it.uniroma3.siw.service.CredentialsService;

@Controller
@RequestMapping("/logged")
public class LoggedRedirectionController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private AuthenticationRefreshService authenticationRefreshService;

    @PostMapping("/diventaGiocatore")
    @PreAuthorize("isAuthenticated()")
    public String redirectGiocatore() {
        User appUser = securityUtils.getCurrentUser();
        if (appUser == null) {
            return "redirect:/login";
        }

        credentialsService.changeToGiocatore(appUser.getId());
        this.authenticationRefreshService.refreshAuthentication(appUser.getId());

        return "redirect:/logged/giocatore/giocatoreIndex";
    }

    @PostMapping("/diventaMaster")
    @PreAuthorize("isAuthenticated()")
    public String redirectMaster() {
        User appUser = securityUtils.getCurrentUser();
        if (appUser == null) {
            return "redirect:/login";
        }

        credentialsService.changeToMaster(appUser.getId());
        this.authenticationRefreshService.refreshAuthentication(appUser.getId());

        return "redirect:/logged/master/masterIndex";
    }
}
