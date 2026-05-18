package com.tms.personnel.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RetardRequest {
    private Long personnelId;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heurePrevue;
    private String motif;
    private boolean justifie;
    private String commentaire;
}
