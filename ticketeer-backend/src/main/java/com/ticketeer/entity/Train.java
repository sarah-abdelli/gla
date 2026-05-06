package com.ticketeer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trains", indexes = {
        @Index(name = "idx_train_numero", columnList = "numero", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    public Train(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Train[" + numero + "]";
    }
}