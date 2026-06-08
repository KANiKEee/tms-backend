package com.tms.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MouvementRequest {
    @NotNull
    private Long produitId;
    @NotNull
    private Long zoneId;
    private Long zoneDestinationId; // for TRANSFERT only
    private Long missionId;         // optional cross-ref
    @NotBlank
    private String type;            // ENTREE, SORTIE, TRANSFERT
    @NotNull
    @Positive
    private Integer quantite;
    private String reference;       // bon de livraison
    private String motif;
}
