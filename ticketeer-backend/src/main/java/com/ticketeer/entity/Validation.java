package com.ticketeer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticketeer.enums.ResultatValidation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations", indexes = {
        @Index(name = "idx_validation_billet", columnList = "billet_uuid"),
        @Index(name = "idx_validation_agent", columnList = "agent_id"),
        @Index(name = "idx_validation_resultat", columnList = "resultat")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultatValidation resultat;

    /** Null si résultat = ACCEPTEE */
    @Column
    private String motifRefus;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "billet_uuid", nullable = false)
    @JsonIgnoreProperties({"validations", "voyageur", "itineraire"})
    private Billet billet;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"tokens", "validations", "motDePasse"})
    private AgentControle agent;

    /** Factory : crée une validation ACCEPTEE */
    public static Validation acceptee(Billet billet, AgentControle agent) {
        Validation v = new Validation();
        v.dateHeure = LocalDateTime.now();
        v.resultat = ResultatValidation.ACCEPTEE;
        v.billet = billet;
        v.agent = agent;
        return v;
    }

    /** Factory : crée une validation REFUSEE avec motif */
    public static Validation refusee(Billet billet, AgentControle agent, String motif) {
        Validation v = new Validation();
        v.dateHeure = LocalDateTime.now();
        v.resultat = ResultatValidation.REFUSEE;
        v.motifRefus = motif;
        v.billet = billet;
        v.agent = agent;
        return v;
    }

    public boolean estAcceptee() {
        return this.resultat == ResultatValidation.ACCEPTEE;
    }
}