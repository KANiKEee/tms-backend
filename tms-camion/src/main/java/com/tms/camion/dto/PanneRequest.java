package com.tms.camion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PanneRequest {
    @NotNull
    private Long camionId;

    private Long chauffeurId;
    private String chauffeurNom;

    @NotBlank
    private String description;

    private String localisation;
}
