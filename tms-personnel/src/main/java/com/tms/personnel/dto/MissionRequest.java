package com.tms.personnel.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MissionRequest {
    private Long chauffeurId;
    private Long camionId;
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
}
