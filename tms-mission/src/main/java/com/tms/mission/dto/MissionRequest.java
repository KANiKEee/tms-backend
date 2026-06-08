package com.tms.mission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MissionRequest {
    @NotNull
    private Long chauffeurId;
    private Long camionId;
    @NotBlank
    private String depart;
    private Double departLat;
    private Double departLng;
    @NotBlank
    private String destination;
    private Double destinationLat;
    private Double destinationLng;
    @NotNull
    private LocalDate dateDebut;
    @NotNull
    private LocalDate dateFin;
    private String description;
    @Positive
    private Integer colisNombre;
    @Positive
    private Double colisHauteur;
    @Positive
    private Double colisLargeur;
    @Positive
    private Double colisLongueur;
    @PositiveOrZero
    private Double volumeTotal;
    private Long produitId;
    private String produitReference;
    private String produitNom;
    private String produitUnite;
    @PositiveOrZero
    private Double produitVolumeUnitaire;
    @PositiveOrZero
    private Double produitPoidsUnitaire;
    @PositiveOrZero
    private Double poidsTotal;
    private Long stockZoneId;
    private String stockZoneNom;
    private Long stockEntrepotId;
    private String stockEntrepotNom;
    private String statut;
}
