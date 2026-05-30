package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProduitResponse {
    private Long id;
    private String reference;
    private String nom;
    private String description;
    private String categorie;
    private String unite;
    private Double poidsUnitaire;
    private Double volumeUnitaire;
    private Integer seuilAlerte;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
