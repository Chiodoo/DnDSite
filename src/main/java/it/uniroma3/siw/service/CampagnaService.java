package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.repository.CampagnaRepository;

@Service
public class CampagnaService {
    
    @Autowired
    private CampagnaRepository campagnaRepository;

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
}   
