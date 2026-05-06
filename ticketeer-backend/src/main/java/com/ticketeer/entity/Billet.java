package com.ticketeer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticketeer.enums.EtatBillet;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "billets", indexes = {
        @Index(name = "idx_billet_uuid", columnList = "uuid"),
        @Index(name = "idx_billet_etat", columnList = "etat"),
        @Index(name = "idx_billet_voyageur", columnList = "voyageur_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(exclude = "validations")
public class Billet {

    @Id
    private String uuid;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatBillet etat;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "voyageur_id", nullable = false)
    private Voyageur voyageur;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "itineraire_id", nullable = false)
    private Itineraire itineraire;

    @JsonIgnore
    @OneToMany(mappedBy = "billet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateHeure DESC")
    private List<Validation> validations = new ArrayList<>();

    public static Billet creer(Voyageur voyageur, Itineraire itineraire) {
        Billet b = new Billet();
        b.uuid = UUID.randomUUID().toString();
        b.dateCreation = LocalDateTime.now();
        b.etat = EtatBillet.VALIDE;
        b.voyageur = voyageur;
        b.itineraire = itineraire;
        return b;
    }

    public String genererQR() { return this.uuid; }
    public boolean estValide() { return this.etat == EtatBillet.VALIDE; }
    public void marquerUtilise() { this.etat = EtatBillet.UTILISE; }
    public void invalider() { this.etat = EtatBillet.INVALIDE; }
    public Validation getDerniereValidation() {
        return validations.isEmpty() ? null : validations.get(0);
    }
}