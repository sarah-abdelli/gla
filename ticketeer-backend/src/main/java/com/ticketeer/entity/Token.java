package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens", indexes = {
        @Index(name = "idx_token_valeur", columnList = "valeur"),
        @Index(name = "idx_token_agent", columnList = "agent_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String valeur;

    @Column(nullable = false)
    private LocalDateTime dateExpiration;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentControle agent;

    public boolean estValide() {
        return LocalDateTime.now().isBefore(this.dateExpiration);
    }

    public boolean estExpire() {
        return !estValide();
    }

    @Override
    public String toString() {
        return "Token[agentId=" + (agent != null ? agent.getId() : "?")
                + ", expire=" + dateExpiration + "]";
    }
}