package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PlanningEventDTO {
    private String id;
    private String type; // ABSENCE, RETARD, CONGE
    private String title;
    private LocalDate start;
    private LocalDate end;
    private String color;
    private String description;
    private String personnelName;
}
