package com.tms.camion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LocationUpdateRequest {
    @NotNull
    private Long missionId;

    @NotNull
    private Long camionId;

    @NotNull
    private Long chauffeurId;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @PositiveOrZero
    private Double accuracy;

    @PositiveOrZero
    private Double speed;

    @DecimalMin("0.0")
    @DecimalMax("360.0")
    private Double heading;
}
