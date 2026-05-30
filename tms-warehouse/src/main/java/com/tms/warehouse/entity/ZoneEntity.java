package com.tms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_zone")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entrepot_id", nullable = false)
    private Long entrepotId;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "type_zone")
    private String type; // RECEPTION, STOCKAGE, EXPEDITION, QUARANTAINE

    @Column(name = "capacite")
    private Double capacite; // m³

    @Column(name = "statut")
    @Builder.Default
    private String statut = "DISPONIBLE"; // DISPONIBLE, PLEIN, MAINTENANCE

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
