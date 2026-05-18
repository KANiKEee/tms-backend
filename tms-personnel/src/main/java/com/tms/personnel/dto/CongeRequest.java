package com.tms.personnel.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CongeRequest {
    private Long personnelId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private String statut;
    private String motif;
    private String commentaire;
}
