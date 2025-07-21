package it.uniroma3.siw.controller.logged.master;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logged/master")
public class LoggedMasterController {



    @GetMapping("/masterIndex")
    public String getHomePage() {
        return "/logged/master/masterIndex";
    }
    
}
