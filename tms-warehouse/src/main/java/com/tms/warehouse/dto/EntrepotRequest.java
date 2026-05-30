package com.tms.warehouse.dto;

import lombok.Data;

@Data
public class EntrepotRequest {
    private String nom;
    private String adresse;
    private String ville;
    private Double latitude;
    private Double longitude;
    private String telephone;
    private String responsable;
    private Double capaciteTotale;
    private String statut;
}
