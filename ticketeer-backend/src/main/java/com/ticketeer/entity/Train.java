package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trains")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;
}
