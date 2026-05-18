package com.tms.camion.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PanneResponse {
    private Long id;
    private Long camionId;
    private String camionImmatricule;
    private Long chauffeurId;
    private String chauffeurNom;
    private String description;
    private String localisation;
    private String statut;
    private String resolution;
    private LocalDateTime dateDeclaration;
    private LocalDateTime dateResolution;
}
