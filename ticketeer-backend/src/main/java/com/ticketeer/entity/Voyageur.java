package com.ticketeer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voyageurs", indexes = {
        @Index(name = "idx_voyageur_email", columnList = "email")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(exclude = "billets")
@JsonIgnoreProperties({"billets", "hibernateLazyInitializer", "handler"})
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

    @JsonIgnore
    @OneToMany(mappedBy = "voyageur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Billet> billets = new ArrayList<>();

    public Voyageur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public long nombreBilletsValides() {
        return billets.stream()
                .filter(b -> b.getEtat() != null && b.estValide())
                .count();
    }
}