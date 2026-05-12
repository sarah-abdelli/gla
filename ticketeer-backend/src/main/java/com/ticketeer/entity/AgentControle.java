package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents_controle", indexes = {
        @Index(name = "idx_agent_login", columnList = "login", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(exclude = "tokens")
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

    // AGENT ou ADMIN
    @Column(nullable = false)
    private String role = "AGENT";

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    public AgentControle(String nom, String login, String motDePasse) {
        this.nom = nom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = "AGENT";
    }

    public AgentControle(String nom, String login, String motDePasse, String role) {
        this.nom = nom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public boolean aTokenActif() {
        return tokens.stream().anyMatch(Token::estValide);
    }
}