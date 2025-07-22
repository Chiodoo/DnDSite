package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
//import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;

@Entity
public class Personaggio {

    @Version
    private Long version;
    
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    //==========================ATTRIBUTES=========================================
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

    @OneToOne(mappedBy = "personaggio", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Image image;

    @ManyToMany
    private List<Campagna> campagne = new ArrayList<>();

    @ManyToOne
    private User user;


    //==========================METHODS=============================================

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazza() {
        return razza;
    }

    public void setRazza(String razza) {
        this.razza = razza;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Long getLivello() {
        return livello;
    }

    public void setLivello(Long livello) {
        this.livello = livello;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /*public Caratteristiche getCaratteristiche() {
        return caratteristiche;
    }

    public void setCaratteristiche(Caratteristiche caratteristiche) {
        this.caratteristiche = caratteristiche;
    }*/

    public List<Campagna> getCampagne() {
        return campagne;
    }

    public void setCampagne(List<Campagna> campagne) {
        this.campagne = campagne;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Personaggio other = (Personaggio) obj;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        return true;
    }

}
