package it.uniroma3.siw.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

@ModelAttribute("userDetails")
public CurrentUserDTO getUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof AnonymousAuthenticationToken || auth == null) {
        return null;
    }

    // 1) Controllo specifico per OAuth2
    if (auth instanceof OAuth2AuthenticationToken oauthToken) {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) oauthToken.getPrincipal();
        String username = extractUsername(oauthUser);
        return new CurrentUserDTO(username, true);
    }

    // 2) Tutti gli altri (form‐login, DaoAuthenticationProvider, ecc.)
    Object principal = auth.getPrincipal();
    if (principal instanceof UserDetails ud) {
        return new CurrentUserDTO(ud.getUsername(), false);
    }

    return null;
}

    private String extractUsername(DefaultOAuth2User oauthUser) {
    // Provo a prendere username in base a chi è il provider
    String username = null;

    // Se c'è un "login" (GitHub)
    if (oauthUser.getAttributes().containsKey("login")) {
        username = (String) oauthUser.getAttributes().get("login");
    }
    // Se c'è una email (Google, Facebook, ecc.)
    else if (oauthUser.getAttributes().containsKey("email")) {
        username = (String) oauthUser.getAttributes().get("email");
    }
    // Se c'è un nome (fallback)
    else if (oauthUser.getAttributes().containsKey("name")) {
        username = (String) oauthUser.getAttributes().get("name");
    }
    else {
        // fallback generico: usa l'id (spesso presente)
        username = oauthUser.getName(); // solitamente l'id provider-unique
    }

    return username;
}

}
