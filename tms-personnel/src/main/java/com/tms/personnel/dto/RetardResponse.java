package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
public class RetardResponse {
    private Long id;
    private Long personnelId;
    private String personnelNom;
    private String personnelPrenom;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heurePrevue;
    private Integer dureeMinutes;
    private String motif;
    private boolean justifie;
    private String commentaire;
    private LocalDateTime createdAt;
}
