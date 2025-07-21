package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Credentials;
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

    // @Autowired
    // public LoggedRedirectionController(SecurityUtils securityUtils,
    //                                    CredentialsService credentialsService) {
    //     this.securityUtils = securityUtils;
    //     this.credentialsService = credentialsService;
    // }

    @PostMapping("/diventaGiocatore")
    @PreAuthorize("isAuthenticated()")
    public String redirectGiocatore() {
        User appUser = securityUtils.getCurrentUser();
        if (appUser == null) {
            return "redirect:/login";
        }

        credentialsService.changeToGiocatore(appUser.getId());
        refreshAuthentication(appUser.getId());

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
        refreshAuthentication(appUser.getId());

        return "redirect:/logged/master/masterIndex";
    }

    /**
     * Ricarica le credenziali aggiornate, crea un UserDetails e aggiorna il SecurityContext
     */
    private void refreshAuthentication(Long userId) {
        Credentials creds = credentialsService.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException(
                "Credenziali non trovate per userId=" + userId));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(creds.getUsername())
            .password(creds.getPassword())
            .roles(creds.getRole())
            .build();

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
