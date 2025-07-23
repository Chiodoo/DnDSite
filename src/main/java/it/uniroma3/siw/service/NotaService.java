package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Nota;
import it.uniroma3.siw.repository.NotaRepository;

@Service
public class NotaService {
    
    @Autowired
    private NotaRepository notaRepository;

    public Nota save(Nota nota) {
        return notaRepository.save(nota);
    }

    public Iterable<Nota> findAll() {
        return notaRepository.findAll();
    }

    public Optional<Nota> findById(Long id) {
        return notaRepository.findById(id);
    }

    public void deleteById(Long id) {
        notaRepository.deleteById(id);
    }
}
