package it.uniroma3.siw.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.repository.CampagnaRepository;
import it.uniroma3.siw.repository.PersonaggioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CampagnaService {
    
    @Autowired
    private CampagnaRepository campagnaRepository;

    @Autowired
    private PersonaggioRepository personaggioRepository;

    public Campagna save(Campagna campagna) {
        return campagnaRepository.save(campagna);
    }

    public Iterable<Campagna> findAll() {
        return campagnaRepository.findAll();
    }

    public Optional<Campagna> findById(Long id) {
        return campagnaRepository.findById(id);
    }

    public void deleteById(Long id) {
        campagnaRepository.deleteById(id);
    }

    /**
     * Restituisce tutte le campagne in cui l'utente ha almeno un personaggio.
     */
    public Set<Campagna> findCampagneByUserId(Long userId) {
        return personaggioRepository.findByUserId(userId).stream()
            .flatMap(p -> p.getCampagne().stream())
            .collect(Collectors.toSet());
    }

    @Transactional
    public void addPartecipante(Long campagnaId, Long personaggioId) {
        Campagna campagna = campagnaRepository.findById(campagnaId)
            .orElseThrow(() -> new EntityNotFoundException("Campagna non trovata"));
        Personaggio p = personaggioRepository.findById(personaggioId)
            .orElseThrow(() -> new EntityNotFoundException("Personaggio non trovato"));

        // 1) sincronizzo entrambi i lati
        if (!p.getCampagne().contains(campagna)) {
            p.getCampagne().add(campagna);
            campagna.getPersonaggi().add(p);
        }
        // 2) persisto SU owner -> Personaggio
        personaggioRepository.save(p);
    }
}   
