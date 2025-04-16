package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Note;

public interface NoteRepository extends CrudRepository<Note, Long>{

}
