package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CongeResponse {
    private Long id;
    private Long personnelId;
    private String personnelNom;
    private String personnelPrenom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private String statut;
    private String motif;
    private Integer nombreJours;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
