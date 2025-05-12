package it.uniroma3.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Caratteristiche {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long forza;

    private Long destrezza;

    private Long intelligenza;

    private Long saggezza;

    private Long carisma;

    private Long costituzione;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForza() {
        return forza;
    }

    public void setForza(Long forza) {
        this.forza = forza;
    }

    public Long getDestrezza() {
        return destrezza;
    }

    public void setDestrezza(Long destrezza) {
        this.destrezza = destrezza;
    }

    public Long getIntelligenza() {
        return intelligenza;
    }

    public void setIntelligenza(Long intelligenza) {
        this.intelligenza = intelligenza;
    }

    public Long getSaggezza() {
        return saggezza;
    }

    public void setSaggezza(Long saggezza) {
        this.saggezza = saggezza;
    }

    public Long getCarisma() {
        return carisma;
    }

    public void setCarisma(Long carisma) {
        this.carisma = carisma;
    }

    public Long getCostituzione() {
        return costituzione;
    }

    public void setCostituzione(Long costituzione) {
        this.costituzione = costituzione;
    }
}
