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
@ToString(exclude = "segments")
public class Itineraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
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

    public LocalTime getHeureDepart() {
        if (segments.isEmpty()) return null;
        return segments.get(0).getHeureDepart();
    }

    public LocalTime getHeureArrivee() {
        if (segments.isEmpty()) return null;
        return segments.get(segments.size() - 1).getHeureArrivee();
    }

    public boolean estCompatible() {
        for (int i = 0; i < segments.size() - 1; i++) {
            LocalTime arrivee = segments.get(i).getHeureArrivee();
            LocalTime departSuivant = segments.get(i + 1).getHeureDepart();
            if (arrivee.isAfter(departSuivant)) return false;
        }
        return true;
    }

    public boolean estDirect() {
        return segments.size() == 1;
    }
}