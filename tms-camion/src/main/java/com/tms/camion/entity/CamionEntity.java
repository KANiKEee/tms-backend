package com.tms.camion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_camion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "immatricule", unique = true, nullable = false)
    private String immatricule;

    @Column(name = "marque", nullable = false)
    private String marque;

    @Column(name = "modele")
    private String modele;

    @Column(name = "nature")
    private String nature; // Remorque, Tracteur, Porteur, Semi-remorque

    @Column(name = "type_camion")
    private String type; // Frigorifique, Bâché, Citerne, Plateau, Fourgon

    @Column(name = "volume")
    private Double volume; // m³

    @Column(name = "poids_max")
    private Double poidsMax; // tonnes

    @Column(name = "carburant")
    private String carburant; // Diesel, Essence, Électrique, Hybride

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "kilometrage")
    private Long kilometrage;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "DISPONIBLE"; // DISPONIBLE, EN_MISSION, EN_PANNE, EN_MAINTENANCE, HORS_SERVICE

    @Column(name = "date_achat")
    private LocalDate dateAchat;

    @Column(name = "date_prochain_ct")
    private LocalDate dateProchainCT;

    @Column(name = "assurance_expiration")
    private LocalDate assuranceExpiration;

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
