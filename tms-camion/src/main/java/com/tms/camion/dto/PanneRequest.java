package com.tms.camion.dto;

import lombok.Data;

@Data
public class PanneRequest {
    private Long camionId;
    private Long chauffeurId;
    private String chauffeurNom;
    private String description;
    private String localisation;
}
