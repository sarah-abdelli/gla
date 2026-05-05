package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voyageurs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Voyageur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL)
    private List<Billet> billets = new ArrayList<>();
}
