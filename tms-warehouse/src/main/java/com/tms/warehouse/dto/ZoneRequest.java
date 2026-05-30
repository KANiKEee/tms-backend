package com.tms.warehouse.dto;

import lombok.Data;

@Data
public class ZoneRequest {
    private String nom;
    private String type; // RECEPTION, STOCKAGE, EXPEDITION, QUARANTAINE
    private Double capacite;
    private String statut;
}
