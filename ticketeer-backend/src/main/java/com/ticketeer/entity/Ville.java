package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "villes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;
}
