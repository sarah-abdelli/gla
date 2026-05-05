package com.ticketeer.entity;

import com.ticketeer.enums.ResultatValidation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "validations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultatValidation resultat;

    @Column
    private String motifRefus; // null si ACCEPTEE

    @ManyToOne(optional = false)
    @JoinColumn(name = "billet_uuid")
    private Billet billet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agent_id")
    private AgentControle agent;
}
