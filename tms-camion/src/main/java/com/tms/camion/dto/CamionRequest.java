package com.tms.camion.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CamionRequest {
    private String immatricule;
    private String marque;
    private String modele;
    private String nature;
    private String type;
    private Double volume;
    private Double poidsMax;
    private String carburant;
    private Integer annee;
    private Long kilometrage;
    private String statut;
    private LocalDate dateAchat;
    private LocalDate dateProchainCT;
    private LocalDate assuranceExpiration;
}
