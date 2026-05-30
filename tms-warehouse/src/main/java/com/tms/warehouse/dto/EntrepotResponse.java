package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EntrepotResponse {
    private Long id;
    private String nom;
    private String adresse;
    private String ville;
    private Double latitude;
    private Double longitude;
    private String telephone;
    private String responsable;
    private Double capaciteTotale;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
