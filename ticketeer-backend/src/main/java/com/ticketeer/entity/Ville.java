package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "villes", indexes = {
        @Index(name = "idx_ville_nom", columnList = "nom", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;

    public Ville(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Ville[" + nom + "]";
    }
}