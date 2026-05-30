package com.tms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_mouvement_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @Column(name = "mission_id")
    private Long missionId; // nullable cross-reference to tms-mission

    @Column(name = "type_mouvement", nullable = false)
    private String type; // ENTREE, SORTIE, TRANSFERT

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "reference")
    private String reference; // bon de livraison / delivery note

    @Column(name = "motif", length = 500)
    private String motif;

    @Column(name = "effectue_par")
    private String effectuePar; // username from JWT

    @Builder.Default
    @Column(name = "date_heure", updatable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();
}
