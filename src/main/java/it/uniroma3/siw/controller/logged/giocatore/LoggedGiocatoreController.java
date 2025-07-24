package it.uniroma3.siw.controller.logged.giocatore;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.controller.CurrentUserDTO;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreController {

    private final UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private SecurityUtils securityUtils;

    LoggedGiocatoreController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/giocatoreIndex")
    public String getHomePage() {
        return "/logged/giocatore/giocatoreIndex";
    }

    @GetMapping("/account")
    public String getAccount(Model model) {

        Credentials credentials = credentialsService.findByUserId(securityUtils.getCurrentUser().getId()).orElse(null);
        if (credentials == null) {
            return "redirect:/logged/giocatore/giocatoreIndex";
        }

        model.addAttribute("credentials", credentials);
        return "/logged/giocatore/account";
    }

    @GetMapping("/modificaAccount")
    public String getModificaAccount(Model model, RedirectAttributes redirectAttrs, @ModelAttribute("userDetails") CurrentUserDTO currentUser) {
        Credentials credentials = credentialsService.findByUserId(securityUtils.getCurrentUser().getId()).orElse(null);
        if (credentials == null) {
            return "redirect:/logged/giocatore/giocatoreIndex";
        }

        if (currentUser != null && currentUser.isOAuth2()) {
            redirectAttrs.addFlashAttribute("errorMessage",
                "Non puoi modificare le credenziali di un account OAuth2.");
            return "redirect:/logged/giocatore/account";
        }

        model.addAttribute("credentials", credentials);
        return "/logged/giocatore/modificaAccount";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials,
                                Model model, RedirectAttributes redirectAttrs) {
        Credentials current = credentialsService.getCurrentCredentials();

        if (current == null || !updatedCredentials.getId().equals(current.getId())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento delle credenziali.");
            return "redirect:/logged/giocatore/account";
        }

        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);
        //riautentica l'utente con le nuove credenziali
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());

        model.addAttribute("credentials", updatedCredentials);
        return "redirect:/logged/giocatore/account";
    }

    
    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model,
                                HttpServletRequest request, HttpServletResponse response) {
        Credentials credentials = credentialsService.getCredentials(id);
        if (credentials != null) {
            credentialsService.deleteCredentials(id);

            // Esegui logout manuale
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            return "redirect:/"; 
        }
        model.addAttribute("error", "Account not found.");
        return "error";
    }
}
