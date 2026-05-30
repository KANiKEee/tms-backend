package com.tms.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ZoneResponse {
    private Long id;
    private Long entrepotId;
    private String nom;
    private String type;
    private Double capacite;
    private String statut;
    private LocalDateTime createdAt;
}
