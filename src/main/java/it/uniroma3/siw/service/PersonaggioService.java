package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.repository.PersonaggioRepository;

@Service
public class PersonaggioService {

    @Autowired
    private PersonaggioRepository personaggioRepository;

    public Personaggio getPersonaggioById(Long id) {
        return personaggioRepository.findById(id).get();
    }

    public Iterable<Personaggio> getAllPersonaggi() {
        return personaggioRepository.findAll();
    }

    public void savePersonaggio(Personaggio personaggio) {
        personaggioRepository.save(personaggio);
    }

}
