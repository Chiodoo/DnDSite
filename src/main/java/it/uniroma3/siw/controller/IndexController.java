package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.security.AuthorizationService;

@Controller
public class IndexController {

	@Autowired AuthorizationService authorizationService;

    @GetMapping(value = "/") 
	public String index(Model model) {

		if(this.authorizationService.isAdmin()) {
			return "admin/indexAdmin";
		} else if(this.authorizationService.isMaster()) {
			return "logged/master/masterIndex";
		} else if(this.authorizationService.isGiocatore()) {
			return "logged/giocatore/giocatoreIndex";
		}

		return "index";
	}

    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {

		if(this.authorizationService.isAdmin()) {
			return "admin/indexAdmin";
		} else if(this.authorizationService.isMaster()) {
			return "logged/master/masterIndex";
		} else if(this.authorizationService.isGiocatore()) {
			return "logged/giocatore/giocatoreIndex";
		}
		return "logged/success";
    }
    
}
