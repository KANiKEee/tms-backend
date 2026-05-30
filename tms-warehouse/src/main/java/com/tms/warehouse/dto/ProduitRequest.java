package com.tms.warehouse.dto;

import lombok.Data;

@Data
public class ProduitRequest {
    private String reference;
    private String nom;
    private String description;
    private String categorie; // PALETTE, CARTON, VRAC, FRAGILE, DANGEREUX
    private String unite;     // KG, TONNE, PIECE, PALETTE, LITRE
    private Double poidsUnitaire;
    private Double volumeUnitaire;
    private Integer seuilAlerte;
}
