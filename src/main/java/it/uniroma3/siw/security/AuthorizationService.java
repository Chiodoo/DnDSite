package it.uniroma3.siw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class AuthorizationService {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Verifica se l'utente autenticato ha il ruolo ADMIN.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication instanceof OAuth2AuthenticationToken) {
            // Utente OAuth2 → trattato come utente normale, non admin
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Credentials credentials = credentialsService.getCredentials(username);
            return credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole());
        }

        return false;
    }

        /**
     * Verifica se l'utente autenticato ha il ruolo MASTER.
     */
    public boolean isMaster() {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            return false;
        }
        Credentials credentials = credentialsService.findByUserId(user.getId()).orElse(null);
        return credentials != null && Credentials.MASTER_ROLE.equals(credentials.getRole());
    }

        /**
     * Verifica se l'utente autenticato ha il ruolo GIOCATORE.
     */
    public boolean isGiocatore() {
        User user = securityUtils.getCurrentUser();
        if (user == null) {
            return false;
        }
        Credentials credentials = credentialsService.findByUserId(user.getId()).orElse(null);
        return credentials != null && Credentials.GIOCATORE_ROLE.equals(credentials.getRole());
    }

}
