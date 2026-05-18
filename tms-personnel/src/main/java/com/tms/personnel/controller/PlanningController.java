package com.tms.personnel.controller;

import com.tms.personnel.dto.*;
import com.tms.personnel.service.PlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    // ═══ ABSENCES ═══

    @PostMapping("/absences")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbsenceResponse> createAbsence(@RequestBody AbsenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planningService.createAbsence(request));
    }

    @GetMapping("/absences")
    public ResponseEntity<List<AbsenceResponse>> getAllAbsences() {
        return ResponseEntity.ok(planningService.getAllAbsences());
    }

    @DeleteMapping("/absences/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        planningService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }

    // ═══ FICHE D'ACTIVITÉ ═══

    @GetMapping("/fiche-activite")
    public ResponseEntity<FicheActiviteDTO> getFicheActivite(
            @RequestParam Long personnelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(planningService.getFicheActivite(personnelId, start, end));
    }

    // ═══ CALENDAR & STATS ═══

    @GetMapping("/calendar")
    public ResponseEntity<List<PlanningEventDTO>> getCalendarEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(planningService.getCalendarEvents(start, end));
    }

    @GetMapping("/stats")
    public ResponseEntity<PlanningStatsDTO> getStats() {
        return ResponseEntity.ok(planningService.getStats());
    }
}
