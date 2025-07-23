package it.uniroma3.siw.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class AuthenticationRefreshService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public void refreshAuthentication(Long userId) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        // ——— Utenti OAuth2 ———
        if (currentAuth instanceof OAuth2AuthenticationToken oauthToken) {
            // ricava il provider (github, google, ecc)
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            ClientRegistration registration =
                clientRegistrationRepository.findByRegistrationId(registrationId);
            String userNameAttrKey =
                registration.getProviderDetails()
                            .getUserInfoEndpoint()
                            .getUserNameAttributeName();

            // prendi gli attributi grezzi (login, email, ecc)
            Map<String,Object> attributes =
                ((DefaultOAuth2User)oauthToken.getPrincipal()).getAttributes();

            // estrai lo username con la stessa logica di GlobalController
            String username = extractUsername(attributes);

            // da username → recupera il ROLE dal tuo DB
            Credentials creds = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                    "Credenziali non trovate per username=" + username));

            // crea la lista di authority corretta (ROLE_GIOCATORE, ecc)
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + creds.getRole()));

            // ricostruisci il DefaultOAuth2User con le nuove authority
            DefaultOAuth2User newPrincipal =
                new DefaultOAuth2User(authorities, attributes, userNameAttrKey);

            OAuth2AuthenticationToken newAuth =
                new OAuth2AuthenticationToken(
                    newPrincipal,
                    authorities,
                    registrationId);

            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return;
        }

        // ——— Utenti “form-login” ———
        if (currentAuth instanceof UsernamePasswordAuthenticationToken) {
            Credentials creds = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                    "Credenziali non trovate per userId=" + userId));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(creds.getUsername())
                .password(creds.getPassword())
                .roles(creds.getRole())
                .build();

            UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        // altrimenti non tocco l’Authentication
    }

    private String extractUsername(Map<String,Object> attrs) {
        if (attrs.containsKey("login"))        return (String) attrs.get("login");
        else if (attrs.containsKey("email"))   return (String) attrs.get("email");
        else if (attrs.containsKey("name"))    return (String) attrs.get("name");
        else                                   throw new IllegalStateException("Username attribute not found");
    }
}
