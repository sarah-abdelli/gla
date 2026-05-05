package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "segments_trajet")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SegmentTrajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDepart;

    @Column(nullable = false)
    private LocalTime heureDepart;

    @Column(nullable = false)
    private LocalTime heureArrivee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ville_depart_id")
    private Ville villeDepart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ville_arrivee_id")
    private Ville villeArrivee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "train_id")
    private Train train;

    // Vérifie que l'heure de contrôle est dans la fenêtre du segment
    public boolean estDansLaBonnePlage(LocalTime heure) {
        return !heure.isBefore(heureDepart) && !heure.isAfter(heureArrivee);
    }
}
