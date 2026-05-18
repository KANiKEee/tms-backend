package com.tms.personnel.service;

import com.tms.personnel.dto.*;
import com.tms.personnel.entity.*;
import com.tms.personnel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final AbsenceRepository absenceRepository;
    private final RetardRepository retardRepository;
    private final CongeRepository congeRepository;
    private final MissionRepository missionRepository;
    private final PersonnelRepository personnelRepository;
    
    // Delegation Services
    private final AttendanceService attendanceService;
    private final MissionService missionService;

    // ═══ ABSENCES (Delegated) ═══
    public AbsenceResponse createAbsence(AbsenceRequest r) { return attendanceService.createAbsence(r); }
    public List<AbsenceResponse> getAllAbsences() { return attendanceService.getAllAbsences(); }
    public void deleteAbsence(Long id) { attendanceService.deleteAbsence(id); }

    // ═══ RETARDS (Delegated) ═══
    public RetardResponse createRetard(RetardRequest r) { return attendanceService.createRetard(r); }
    public List<RetardResponse> getAllRetards() { return attendanceService.getAllRetards(); }
    public void deleteRetard(Long id) { attendanceService.deleteRetard(id); }

    // ═══ CONGÉS (Delegated) ═══
    public CongeResponse createConge(CongeRequest r) { return attendanceService.createConge(r); }
    public CongeResponse updateCongeStatut(Long id, String s) { return attendanceService.updateCongeStatut(id, s); }
    public List<CongeResponse> getAllConges() { return attendanceService.getAllConges(); }
    public void deleteConge(Long id) { attendanceService.deleteConge(id); }

    // ═══ MISSIONS (Delegated) ═══
    public MissionResponse createMission(MissionRequest r) { return missionService.createMission(r); }
    public List<MissionResponse> getAllMissions() { return missionService.getAllMissions(); }
    public MissionResponse updateMissionStatut(Long id, String s) { return missionService.updateMissionStatut(id, s); }
    public void deleteMission(Long id) { missionService.deleteMission(id); }

    // ═══ FICHE D'ACTIVITÉ ═══

    public FicheActiviteDTO getFicheActivite(Long personnelId, LocalDate start, LocalDate end) {
        PersonnelEntity personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'ID: " + personnelId));

        List<AbsenceResponse> absences = absenceRepository.findByPersonnelIdAndDateRange(personnelId, start, end)
                .stream().map(attendanceService::toAbsenceResponse).collect(Collectors.toList());

        List<RetardResponse> retards = retardRepository.findByPersonnelIdAndDateRange(personnelId, start, end)
                .stream().map(attendanceService::toRetardResponse).collect(Collectors.toList());

        List<CongeResponse> conges = congeRepository.findByPersonnelIdAndDateRange(personnelId, start, end)
                .stream().map(attendanceService::toCongeResponse).collect(Collectors.toList());

        List<MissionResponse> missions = missionRepository.findByDateRange(start, end).stream()
                .filter(m -> m.getChauffeurId().equals(personnelId))
                .map(missionService::toMissionResponse).collect(Collectors.toList());

        long totalJoursAbsence = absences.stream()
                .mapToLong(a -> ChronoUnit.DAYS.between(a.getDateDebut(), a.getDateFin()) + 1)
                .sum();

        long totalMinutesRetard = retards.stream()
                .mapToLong(r -> r.getDureeMinutes() != null ? r.getDureeMinutes() : 0)
                .sum();

        long totalJoursConge = conges.stream()
                .mapToLong(c -> c.getNombreJours() != null ? c.getNombreJours() : ChronoUnit.DAYS.between(c.getDateDebut(), c.getDateFin()) + 1)
                .sum();

        return FicheActiviteDTO.builder()
                .personnelId(personnelId)
                .personnelNom(personnel.getNom())
                .personnelPrenom(personnel.getPrenom())
                .matricule(personnel.getMatricule())
                .poste(personnel.getPosteDeTravail())
                .dateDebut(start)
                .dateFin(end)
                .absences(absences)
                .retards(retards)
                .conges(conges)
                .missions(missions)
                .totalAbsences(absences.size())
                .totalJoursAbsence(totalJoursAbsence)
                .totalRetards(retards.size())
                .totalMinutesRetard(totalMinutesRetard)
                .totalConges(conges.size())
                .totalJoursConge(totalJoursConge)
                .totalMissions(missions.size())
                .build();
    }

    // ═══ CALENDAR & STATS ═══

    public List<PlanningEventDTO> getCalendarEvents(LocalDate start, LocalDate end) {
        List<PlanningEventDTO> events = new ArrayList<>();

        // Add Absences
        absenceRepository.findByDateRange(start, end).forEach(a -> {
            events.add(PlanningEventDTO.builder()
                    .id("ABS-" + a.getId())
                    .type("ABSENCE")
                    .title("Absence: " + personnelName(a.getPersonnelId()))
                    .start(a.getDateDebut())
                    .end(a.getDateFin())
                    .color("#f43f5e") // danger
                    .description(a.getMotif())
                    .personnelName(personnelName(a.getPersonnelId()))
                    .build());
        });

        // Add Retards
        retardRepository.findByDateRange(start, end).forEach(r -> {
            events.add(PlanningEventDTO.builder()
                    .id("RET-" + r.getId())
                    .type("RETARD")
                    .title("Retard: " + personnelName(r.getPersonnelId()))
                    .start(r.getDate())
                    .end(r.getDate())
                    .color("#f59e0b") // warning
                    .description(r.getDureeMinutes() + " min - " + r.getMotif())
                    .personnelName(personnelName(r.getPersonnelId()))
                    .build());
        });

        // Add Conges
        congeRepository.findByDateRange(start, end).forEach(c -> {
            events.add(PlanningEventDTO.builder()
                    .id("CON-" + c.getId())
                    .type("CONGE")
                    .title("Congé: " + personnelName(c.getPersonnelId()))
                    .start(c.getDateDebut())
                    .end(c.getDateFin())
                    .color("#4f6ef7") // primary
                    .description(c.getType() + " (" + c.getStatut() + ")")
                    .personnelName(personnelName(c.getPersonnelId()))
                    .build());
        });

        // Add Missions
        missionRepository.findByDateRange(start, end).forEach(m -> {
            events.add(PlanningEventDTO.builder()
                    .id("MIS-" + m.getId())
                    .type("MISSION")
                    .title("Mission: " + personnelName(m.getChauffeurId()))
                    .start(m.getDateDebut())
                    .end(m.getDateFin())
                    .color("#14b8a6") // teal
                    .description(m.getDepart() + " → " + m.getDestination())
                    .personnelName(personnelName(m.getChauffeurId()))
                    .build());
        });

        return events;
    }

    public PlanningStatsDTO getStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<AbsenceEntity> absences = absenceRepository.findByDateRange(startOfMonth, endOfMonth);
        List<RetardEntity> retards = retardRepository.findByDateRange(startOfMonth, endOfMonth);
        List<CongeEntity> conges = congeRepository.findByStatut("EN_ATTENTE");

        double totalHeuresPrevues = 0;
        List<PersonnelEntity> allPersonnel = personnelRepository.findAll();
        for (PersonnelEntity p : allPersonnel) {
            double dailyHours = p.getHeuresTravailParJour() != null ? p.getHeuresTravailParJour() : 8.0;
            totalHeuresPrevues += dailyHours * 22; 
        }

        double absenceHours = 0;
        for (AbsenceEntity a : absences) {
            PersonnelEntity p = personnelRepository.findById(a.getPersonnelId()).orElse(null);
            double dailyHours = (p != null && p.getHeuresTravailParJour() != null) ? p.getHeuresTravailParJour() : 8.0;
            long days = ChronoUnit.DAYS.between(a.getDateDebut(), a.getDateFin()) + 1;
            absenceHours += dailyHours * days;
        }

        return PlanningStatsDTO.builder()
                .totalAbsences(absences.size())
                .totalRetards(retards.size())
                .totalCongesEnAttente(conges.size())
                .totalHeuresPrevues(totalHeuresPrevues)
                .totalHeuresEffectuees(totalHeuresPrevues - absenceHours)
                .absencesByType(absences.stream().collect(Collectors.groupingBy(AbsenceEntity::getType, Collectors.counting())))
                .retardsByMotif(retards.stream().collect(Collectors.groupingBy(RetardEntity::getMotif, Collectors.counting())))
                .build();
    }

    private String personnelName(Long id) {
        return personnelRepository.findById(id)
                .map(p -> p.getNom() + " " + p.getPrenom())
                .orElse("Inconnu");
    }
}
