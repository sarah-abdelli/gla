package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itineraires")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Itineraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "itineraire_id")
    @OrderColumn(name = "ordre")
    private List<SegmentTrajet> segments = new ArrayList<>();

    public Ville getVilleDepart() {
        if (segments.isEmpty()) return null;
        return segments.get(0).getVilleDepart();
    }

    public Ville getVilleArrivee() {
        if (segments.isEmpty()) return null;
        return segments.get(segments.size() - 1).getVilleArrivee();
    }

    public boolean estCompatible() {
        // Vérifie que les horaires des segments se suivent logiquement
        for (int i = 0; i < segments.size() - 1; i++) {
            LocalTime arrivee = segments.get(i).getHeureArrivee();
            LocalTime departSuivant = segments.get(i + 1).getHeureDepart();
            if (arrivee.isAfter(departSuivant)) return false;
        }
        return true;
    }
}
