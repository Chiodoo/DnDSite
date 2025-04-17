package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Personaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String razza;

    @Column(nullable = false)
    private String classe;

    @Column(nullable = false)
    private Long livello;

    @Column(length = 2000)
    private String descrizione;

    @OneToOne
    private Caratteristiche caratteristiche;

    @ManyToMany
    private List<Campagna> campagne;

}
