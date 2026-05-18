package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FicheActiviteDTO {
    private Long personnelId;
    private String personnelNom;
    private String personnelPrenom;
    private String matricule;
    private String poste;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Detailed lists
    private List<AbsenceResponse> absences;
    private List<RetardResponse> retards;
    private List<CongeResponse> conges;
    private List<MissionResponse> missions;

    // Summary stats
    private int totalAbsences;
    private long totalJoursAbsence;
    private int totalRetards;
    private long totalMinutesRetard;
    private int totalConges;
    private long totalJoursConge;
    private int totalMissions;
}
