package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected UserRepository userRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional(readOnly = true)
    public Credentials findById(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUserId(Long id) {
        return credentialsRepository.findByUserId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

/**
     * Salva (o aggiorna) delle Credentials.
     * - Se non è già presente un ruolo, imposta DEFAULT_ROLE.
     * - Se la password non è già in BCrypt, la codifica.
     */
    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        // 1) Ruolo
        if (credentials.getRole() == null) {
            credentials.setRole(Credentials.DEFAULT_ROLE);
        }

        // 2) Password: se non è già un hash BCrypt (inizia con "$2"), allora codificala
        String pwd = credentials.getPassword();
        if (pwd != null && !pwd.startsWith("$2")) {
            credentials.setPassword(passwordEncoder.encode(pwd));
        }

        return credentialsRepository.save(credentials);
    }

 @Transactional
    public void changeToGiocatore(Long userId) {
        Credentials credentials = findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Credenziali non trovate per userId=" + userId));
        credentials.setRole(Credentials.GIOCATORE_ROLE);
        saveCredentials(credentials);
    }

    @Transactional
    public void changeToMaster(Long userId) {
        Credentials credentials = findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Credenziali non trovate per userId=" + userId));
        credentials.setRole(Credentials.MASTER_ROLE);
        saveCredentials(credentials);
    }

    @Transactional
    public Credentials getCurrentCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Credentials credentials = this.getCredentials(username);
            return credentials;
        }
        return null;
    }

    @Transactional
    public void updateCredentials(Credentials credentials) {
        Credentials existingCredentials = this.getCredentials(credentials.getId());
        if(existingCredentials != null) {
            existingCredentials.setUsername(credentials.getUsername());

            if(credentials.getPassword() != null && !credentials.getPassword().isBlank()) {
                existingCredentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
            }
            existingCredentials.setRole(credentials.getRole());

            //recupera l'utente associato e ricollegalo
            if(existingCredentials.getUser() != null) {
                existingCredentials.getUser().setCredentials(existingCredentials);
            }
            this.credentialsRepository.save(existingCredentials);
        }
    }

    @Transactional
    public void autoLogin(String username, String rawPassword) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, rawPassword);
        Authentication auth = authenticationManager.authenticate(token);

        if(auth.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            throw new UsernameNotFoundException("Credenziali non valide per l'utente: " + username);
        }
       
    }

    @Transactional
    public void deleteCredentials(Long id) {
        Credentials credentials = this.getCredentials(id);
        if (credentials != null) {
           User user = credentials.getUser();
            if (user != null) {
                user.setCredentials(null); // Rimuove il collegamento tra l'utente e le credenziali
                userRepository.delete(user); // Elimina l'utente se necessario
            }
            this.credentialsRepository.delete(credentials);
        }
    }
}
