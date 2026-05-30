package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StockResponse {
    private Long id;
    private Long produitId;
    private String produitNom;
    private String produitReference;
    private Long zoneId;
    private String zoneNom;
    private Long entrepotId;
    private String entrepotNom;
    private Integer quantite;
    private Integer seuilAlerte;
    private boolean enAlerte;
    private LocalDateTime derniereMaj;
}
