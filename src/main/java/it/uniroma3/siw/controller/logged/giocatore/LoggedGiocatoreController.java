package it.uniroma3.siw.controller.logged.giocatore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/logged/giocatore")
public class LoggedGiocatoreController {

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/giocatoreIndex")
    public String getHomePage() {
        return "/logged/giocatore/giocatoreIndex";
    }

    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if (credentials == null) {
            return "redirect:/logged/giocatore/giocatoreIndex";
        }

        model.addAttribute("credentials", credentials);
        return "/logged/giocatore/account";
    }

    @GetMapping("/modificaAccount")
    public String getModificaAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        if (credentials == null) {
            return "redirect:/logged/giocatore/giocatoreIndex";
        }

        model.addAttribute("credentials", credentials);
        return "/logged/giocatore/modificaAccount";
    }

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials, Model model) {
        Credentials current = credentialsService.getCurrentCredentials();

        if (current == null || !updatedCredentials.getId().equals(current.getId())) {
            model.addAttribute("error", "Errore durante l'aggiornamento delle credenziali.");
            return "redirect:/logged/giocatore/account";
        }
        credentialsService.updateCredentials(updatedCredentials);
        //riautentica l'utente con le nuove credenziali
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());

        model.addAttribute("credentials", updatedCredentials);
        return "redirect:/logged/giocatore/account";
    }

    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);
        if(credentials != null){
            credentialsService.deleteCredentials(id);
            return "redirect:/logout"; // Redirect to logout after deletion
        } 
        model.addAttribute("error", "Account not found.");
        return "error";
    }


}
