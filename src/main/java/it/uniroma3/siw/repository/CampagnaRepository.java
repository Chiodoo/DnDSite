package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Campagna;

public interface CampagnaRepository extends CrudRepository<Campagna, Long>{

    Iterable<Campagna> findAllByMasterId(Long id);
}
