package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Personaggio;
import it.uniroma3.siw.repository.PersonaggioRepository;
import it.uniroma3.siw.service.storage.ImageStorageService;

@Service
public class PersonaggioService {

    @Autowired
    private PersonaggioRepository personaggioRepository;

    @Autowired
    private ImageStorageService imageStorageService;


    public Personaggio save(Personaggio personaggio) {
        return personaggioRepository.save(personaggio);
    }

    public Iterable<Personaggio> findAll() {
        return personaggioRepository.findAll();
    }

    public Optional<Personaggio> findById(Long id) {
        return personaggioRepository.findById(id);
    }
    
    
    public void deleteById(Long id) {
        personaggioRepository.deleteById(id);
    }

    @Transactional
    public Personaggio saveWithImage(Personaggio personaggio, MultipartFile imageFile) throws IOException {

        // Salva il personaggio inizialmente per avere un ID (serve per il path immagine)
        personaggio = this.personaggioRepository.save(personaggio);

        // Gestione dell'immagine
        if (imageFile != null && !imageFile.isEmpty()) {
            String path = this.imageStorageService.store(imageFile, "personaggio/" + personaggio.getId());

            Image image = new Image();
            image.setPath(path);
            image.setPersonaggio(personaggio);   
            personaggio.setImage(image);     
        }

        // Salva autore con immagine associata
        return this.personaggioRepository.save(personaggio);
    }
    public List<Personaggio> findbyCampagna(Campagna campagna) {
        return personaggioRepository.findByCampagneContains(campagna);
    }

    public List<Personaggio> findByUserId(Long id) {
        return personaggioRepository.findByUserId(id);
    }
}
