package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "agent_id")
    private AgentControle agent;

    public boolean estValide() {
        return LocalDateTime.now().isBefore(this.dateExpiration);
    }

    public boolean estExpire() {
        return !estValide();
    }
}
