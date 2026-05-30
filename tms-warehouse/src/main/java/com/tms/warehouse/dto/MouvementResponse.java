package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MouvementResponse {
    private Long id;
    private Long produitId;
    private String produitNom;
    private String produitReference;
    private Long zoneId;
    private String zoneNom;
    private Long missionId;
    private String type;
    private Integer quantite;
    private String reference;
    private String motif;
    private String effectuePar;
    private LocalDateTime dateHeure;
}
