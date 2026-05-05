package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents_controle")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AgentControle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String motDePasse;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    private List<Token> tokens = new ArrayList<>();
}
