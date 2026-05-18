package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class PlanningStatsDTO {
    private long totalAbsences;
    private long totalRetards;
    private long totalCongesEnAttente;
    private double totalHeuresPrevues;
    private double totalHeuresEffectuees;
    private Map<String, Long> absencesByType;
    private Map<String, Long> retardsByMotif;
}
