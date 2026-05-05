package com.ticketeer.entity;

import com.ticketeer.enums.EtatBillet;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "billets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Billet {

    @Id
    private String uuid;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatBillet etat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voyageur_id")
    private Voyageur voyageur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "itineraire_id")
    private Itineraire itineraire;

    @OneToMany(mappedBy = "billet", cascade = CascadeType.ALL)
    private List<Validation> validations = new ArrayList<>();

    // Génère un UUID unique pour le billet
    public static String genererUUID() {
        return UUID.randomUUID().toString();
    }

    public boolean estValide() {
        return this.etat == EtatBillet.VALIDE;
    }

    public void marquerUtilise() {
        this.etat = EtatBillet.UTILISE;
    }

    public void invalider() {
        this.etat = EtatBillet.INVALIDE;
    }
}
