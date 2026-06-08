package com.tms.camion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_panne")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanneEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "camion_id", nullable = false)
    private Long camionId;

    @Column(name = "camion_immatricule")
    private String camionImmatricule;

    @Column(name = "chauffeur_id")
    private Long chauffeurId;

    @Column(name = "chauffeur_nom")
    private String chauffeurNom;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "localisation")
    private String localisation;

    @Column(name = "statut")
    @Builder.Default
    private String statut = "DECLAREE"; // DECLAREE, APPROUVEE, EN_COURS, RESOLUE

    @Column(name = "resolution", length = 2000)
    private String resolution;

    @Builder.Default
    @Column(name = "date_declaration", updatable = false)
    private LocalDateTime dateDeclaration = LocalDateTime.now();

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;
}
