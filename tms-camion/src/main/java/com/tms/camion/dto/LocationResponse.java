package com.tms.camion.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponse {
    private Long id;
    private Long camionId;
    private String camionImmatricule;
    private Long chauffeurId;
    private String chauffeurNom;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private LocalDateTime timestamp;
}
