package com.tms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_entrepot")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepotEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "ville")
    private String ville;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "capacite_totale")
    private Double capaciteTotale; // m³

    @Column(name = "statut")
    @Builder.Default
    private String statut = "ACTIF"; // ACTIF, INACTIF, EN_MAINTENANCE

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
