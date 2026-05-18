package com.tms.personnel.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AbsenceRequest {
    private Long personnelId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private String type;
    private boolean justificatif;
    private String commentaire;
}
