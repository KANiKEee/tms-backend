package com.tms.personnel.service;

import com.tms.personnel.dto.*;
import com.tms.personnel.entity.*;
import com.tms.personnel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AbsenceRepository absenceRepository;
    private final RetardRepository retardRepository;
    private final CongeRepository congeRepository;
    private final PersonnelRepository personnelRepository;

    // ═══ ABSENCES ═══

    @Transactional
    public AbsenceResponse createAbsence(AbsenceRequest request) {
        AbsenceEntity entity = AbsenceEntity.builder()
                .personnelId(request.getPersonnelId())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .motif(request.getMotif())
                .type(request.getType())
                .justificatif(request.isJustificatif())
                .commentaire(request.getCommentaire())
                .build();
        return toAbsenceResponse(absenceRepository.save(entity));
    }

    public List<AbsenceResponse> getAllAbsences() {
        return absenceRepository.findAll().stream()
                .map(this::toAbsenceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }

    // ═══ RETARDS ═══

    @Transactional
    public RetardResponse createRetard(RetardRequest request) {
        Integer duration = 0;
        if (request.getHeureArrivee() != null && request.getHeurePrevue() != null) {
            long diff = java.time.Duration.between(request.getHeurePrevue(), request.getHeureArrivee()).toMinutes();
            duration = (int) Math.max(0, diff);
        }

        RetardEntity entity = RetardEntity.builder()
                .personnelId(request.getPersonnelId())
                .date(request.getDate())
                .heureArrivee(request.getHeureArrivee())
                .heurePrevue(request.getHeurePrevue())
                .dureeMinutes(duration)
                .motif(request.getMotif())
                .justifie(request.isJustifie())
                .commentaire(request.getCommentaire())
                .build();
        return toRetardResponse(retardRepository.save(entity));
    }

    public List<RetardResponse> getAllRetards() {
        return retardRepository.findAll().stream()
                .map(this::toRetardResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRetard(Long id) {
        retardRepository.deleteById(id);
    }

    // ═══ CONGÉS ═══

    @Transactional
    public CongeResponse createConge(CongeRequest request) {
        long days = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin()) + 1;
        
        CongeEntity entity = CongeEntity.builder()
                .personnelId(request.getPersonnelId())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .type(request.getType())
                .statut(request.getStatut() != null ? request.getStatut() : "EN_ATTENTE")
                .motif(request.getMotif())
                .nombreJours((int) days)
                .commentaire(request.getCommentaire())
                .build();

        if ("APPROUVE".equals(entity.getStatut())) {
            deductCongeBalance(entity.getPersonnelId(), entity.getNombreJours());
        }

        return toCongeResponse(congeRepository.save(entity));
    }

    @Transactional
    public CongeResponse updateCongeStatut(Long id, String statut) {
        CongeEntity entity = congeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Congé non trouvé"));
        
        String oldStatut = entity.getStatut();
        entity.setStatut(statut);

        if ("APPROUVE".equals(statut) && !"APPROUVE".equals(oldStatut)) {
            deductCongeBalance(entity.getPersonnelId(), entity.getNombreJours());
        } else if (!"APPROUVE".equals(statut) && "APPROUVE".equals(oldStatut)) {
            deductCongeBalance(entity.getPersonnelId(), -entity.getNombreJours());
        }

        return toCongeResponse(congeRepository.save(entity));
    }

    private void deductCongeBalance(Long personnelId, Integer days) {
        PersonnelEntity personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        Integer currentBalance = personnel.getSoldeConge() != null ? personnel.getSoldeConge() : 25;
        personnel.setSoldeConge(currentBalance - days);
        personnelRepository.save(personnel);
    }

    public List<CongeResponse> getAllConges() {
        return congeRepository.findAll().stream()
                .map(this::toCongeResponse)
                .collect(Collectors.toList());
    }

    public List<CongeResponse> getCongesByPersonnel(Long personnelId) {
        return congeRepository.findByPersonnelId(personnelId).stream()
                .map(this::toCongeResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteConge(Long id) {
        congeRepository.deleteById(id);
    }

    // ═══ MAPPERS ═══

    public AbsenceResponse toAbsenceResponse(AbsenceEntity e) {
        PersonnelEntity p = personnelRepository.findById(e.getPersonnelId()).orElse(null);
        return AbsenceResponse.builder()
                .id(e.getId())
                .personnelId(e.getPersonnelId())
                .personnelNom(p != null ? p.getNom() : "Inconnu")
                .personnelPrenom(p != null ? p.getPrenom() : "")
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .motif(e.getMotif())
                .type(e.getType())
                .justificatif(e.isJustificatif())
                .commentaire(e.getCommentaire())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public RetardResponse toRetardResponse(RetardEntity e) {
        PersonnelEntity p = personnelRepository.findById(e.getPersonnelId()).orElse(null);
        return RetardResponse.builder()
                .id(e.getId())
                .personnelId(e.getPersonnelId())
                .personnelNom(p != null ? p.getNom() : "Inconnu")
                .personnelPrenom(p != null ? p.getPrenom() : "")
                .date(e.getDate())
                .heureArrivee(e.getHeureArrivee())
                .heurePrevue(e.getHeurePrevue())
                .dureeMinutes(e.getDureeMinutes())
                .motif(e.getMotif())
                .justifie(e.isJustifie())
                .commentaire(e.getCommentaire())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public CongeResponse toCongeResponse(CongeEntity e) {
        PersonnelEntity p = personnelRepository.findById(e.getPersonnelId()).orElse(null);
        return CongeResponse.builder()
                .id(e.getId())
                .personnelId(e.getPersonnelId())
                .personnelNom(p != null ? p.getNom() : "Inconnu")
                .personnelPrenom(p != null ? p.getPrenom() : "")
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .type(e.getType())
                .statut(e.getStatut())
                .motif(e.getMotif())
                .nombreJours(e.getNombreJours())
                .commentaire(e.getCommentaire())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
