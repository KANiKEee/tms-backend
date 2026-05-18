package com.tms.camion.dto;

import lombok.Data;

@Data
public class LocationUpdateRequest {
    private Long camionId;
    private String camionImmatricule;
    private Long chauffeurId;
    private String chauffeurNom;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
}
