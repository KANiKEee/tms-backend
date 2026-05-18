package com.tms.personnel.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PersonnelStats {
    private long totalPersonnel;
    private long totalChauffeurs;
    private long contratActifs;
    private long contratExpires;
    private long permisExpirantSoon;
    private long visiteMedicaleSoon;
    private long entretiensSoon;
    private long cartesSejourExpirantSoon;
    private long formationsExpirees;
    private double tauxAbsenceMoyen;
    private Map<String, Long> parTypeRessource;
    private Map<String, Long> parNatureContrat;
    private Map<String, Long> parVille;
    private Map<String, Long> parTypePermis;
    private Map<String, Long> parNationalite;
}
