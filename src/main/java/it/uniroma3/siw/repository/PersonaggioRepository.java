package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Campagna;
import it.uniroma3.siw.model.Personaggio;

public interface PersonaggioRepository extends CrudRepository<Personaggio, Long>{

    List<Personaggio> findByCampagneContains(Campagna campagna);

    List<Personaggio> findByUserId(Long id);
}
