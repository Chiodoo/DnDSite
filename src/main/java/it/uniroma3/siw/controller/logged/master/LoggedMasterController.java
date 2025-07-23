package it.uniroma3.siw.controller.logged.master;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/logged/master")
public class LoggedMasterController {

    private final UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    LoggedMasterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/masterIndex")
    public String getHomePage() {
        return "/logged/master/masterIndex";
    }
    
    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if (credentials == null) {
            return "redirect:/logged/master/masterIndex";
        }

        model.addAttribute("credentials", credentials);
        return "/logged/master/account";
    }

    @GetMapping("/modificaAccount")
    public String getModificaAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if (credentials == null) {
            return "redirect:/logged/master/masterIndex";
        }

        if (credentials.getUser() == null) {
        credentials.setUser(new User());
    }
        model.addAttribute("credentials", credentials);
        return "/logged/master/modificaAccount";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials, 
                                Model model) {
        Credentials current = credentialsService.getCurrentCredentials();

        if (current == null || !updatedCredentials.getId().equals(current.getId())) {
            model.addAttribute("error", "Errore durante l'aggiornamento delle credenziali.");
            return "redirect:/logged/master/masterIndex";
        }

        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);
        
        //riautentica l'utente con le nuove credenziali
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());

        model.addAttribute("credentials", updatedCredentials);
        return "redirect:/logged/master/account";
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

            return "redirect:/"; // oppure una pagina di conferma
        }
        model.addAttribute("error", "Account not found.");
        return "error";
    }
    
}
