package com.tms.warehouse.dto;

import lombok.Data;

@Data
public class MouvementRequest {
    private Long produitId;
    private Long zoneId;
    private Long zoneDestinationId; // for TRANSFERT only
    private Long missionId;         // optional cross-ref
    private String type;            // ENTREE, SORTIE, TRANSFERT
    private Integer quantite;
    private String reference;       // bon de livraison
    private String motif;
}
