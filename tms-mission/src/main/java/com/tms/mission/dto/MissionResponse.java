package com.tms.mission.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MissionResponse {
    private Long id;
    private Long chauffeurId;
    private String chauffeurNom;
    private String chauffeurPrenom;
    private Long camionId;
    private String camionImmatricule;
    private String depart;
    private Double departLat;
    private Double departLng;
    private String destination;
    private Double destinationLat;
    private Double destinationLng;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private Integer colisNombre;
    private Double colisHauteur;
    private Double colisLargeur;
    private Double colisLongueur;
    private Double volumeTotal;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
