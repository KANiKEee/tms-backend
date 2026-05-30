package com.tms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_produit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true, nullable = false)
    private String reference; // SKU

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "categorie")
    private String categorie; // PALETTE, CARTON, VRAC, FRAGILE, DANGEREUX

    @Column(name = "unite")
    private String unite; // KG, TONNE, PIECE, PALETTE, LITRE

    @Column(name = "poids_unitaire")
    private Double poidsUnitaire;

    @Column(name = "volume_unitaire")
    private Double volumeUnitaire;

    @Column(name = "seuil_alerte")
    private Integer seuilAlerte; // minimum stock level

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
