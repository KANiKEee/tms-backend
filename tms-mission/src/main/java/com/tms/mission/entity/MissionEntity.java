package com.tms.mission.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_mission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chauffeur_id", nullable = false)
    private Long chauffeurId;

    @Column(name = "camion_id")
    private Long camionId;

    @Column(name = "depart", nullable = false)
    private String depart;

    @Column(name = "depart_lat")
    private Double departLat;

    @Column(name = "depart_lng")
    private Double departLng;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "destination_lat")
    private Double destinationLat;

    @Column(name = "destination_lng")
    private Double destinationLng;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "description", length = 1000)
    private String description;

    // ═══ COLIS ═══
    @Column(name = "colis_nombre")
    private Integer colisNombre;

    @Column(name = "colis_hauteur")
    private Double colisHauteur;

    @Column(name = "colis_largeur")
    private Double colisLargeur;

    @Column(name = "colis_longueur")
    private Double colisLongueur;

    @Column(name = "volume_total")
    private Double volumeTotal;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "PLANIFIEE";

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
