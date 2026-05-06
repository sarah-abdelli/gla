package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "segments_trajet", indexes = {
        @Index(name = "idx_segment_depart_arrivee", columnList = "ville_depart_id, ville_arrivee_id"),
        @Index(name = "idx_segment_date", columnList = "date_depart"),
        @Index(name = "idx_segment_train", columnList = "train_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SegmentTrajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_depart", nullable = false)
    private LocalDate dateDepart;

    @Column(nullable = false)
    private LocalTime heureDepart;

    @Column(nullable = false)
    private LocalTime heureArrivee;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_depart_id", nullable = false)
    private Ville villeDepart;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_arrivee_id", nullable = false)
    private Ville villeArrivee;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    /** Vérifie que l'heure de contrôle est dans la fenêtre horaire */
    public boolean estDansLaBonnePlage(LocalTime heure) {
        return !heure.isBefore(heureDepart) && !heure.isAfter(heureArrivee);
    }

    /** Surcharge : vérifie aussi la date */
    public boolean estDansLaBonnePlage(LocalDate date, LocalTime heure) {
        return dateDepart.equals(date) && estDansLaBonnePlage(heure);
    }

    /** Vérifie que ce segment est associé au train donné */
    public boolean estAssocieAuTrain(String numeroTrain) {
        return this.train != null && this.train.getNumero().equals(numeroTrain);
    }

    @Override
    public String toString() {
        return String.format("Segment[%s→%s, train=%s, %s %s-%s]",
                villeDepart != null ? villeDepart.getNom() : "?",
                villeArrivee != null ? villeArrivee.getNom() : "?",
                train != null ? train.getNumero() : "?",
                dateDepart, heureDepart, heureArrivee);
    }
}