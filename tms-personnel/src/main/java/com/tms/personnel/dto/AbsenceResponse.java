package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AbsenceResponse {
    private Long id;
    private Long personnelId;
    private String personnelNom;
    private String personnelPrenom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private String type;
    private boolean justificatif;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
