package com.tms.personnel.controller;

import com.tms.personnel.dto.*;
import com.tms.personnel.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ═══ ABSENCES ═══
    @PostMapping("/absences")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AbsenceResponse> createAbsence(@RequestBody AbsenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAbsence(request));
    }

    @GetMapping("/absences")
    public ResponseEntity<List<AbsenceResponse>> getAllAbsences() {
        return ResponseEntity.ok(attendanceService.getAllAbsences());
    }

    @DeleteMapping("/absences/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        attendanceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }

    // ═══ RETARDS ═══
    @PostMapping("/retards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RetardResponse> createRetard(@RequestBody RetardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createRetard(request));
    }

    @GetMapping("/retards")
    public ResponseEntity<List<RetardResponse>> getAllRetards() {
        return ResponseEntity.ok(attendanceService.getAllRetards());
    }

    @DeleteMapping("/retards/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRetard(@PathVariable Long id) {
        attendanceService.deleteRetard(id);
        return ResponseEntity.noContent().build();
    }

    // ═══ CONGÉS ═══
    @PostMapping("/conges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CongeResponse> createConge(@RequestBody CongeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createConge(request));
    }

    @GetMapping("/conges")
    public ResponseEntity<List<CongeResponse>> getAllConges() {
        return ResponseEntity.ok(attendanceService.getAllConges());
    }

    // ═══ CONGÉ REQUESTS (accessible by any authenticated user) ═══
    @GetMapping("/conges/personnel/{personnelId}")
    public ResponseEntity<List<CongeResponse>> getCongesByPersonnel(@PathVariable Long personnelId) {
        return ResponseEntity.ok(attendanceService.getCongesByPersonnel(personnelId));
    }

    @PostMapping("/conges/request")
    public ResponseEntity<CongeResponse> requestConge(@RequestBody CongeRequest request) {
        request.setStatut("EN_ATTENTE");
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createConge(request));
    }

    @PutMapping("/conges/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CongeResponse> updateCongeStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(attendanceService.updateCongeStatut(id, statut));
    }

    @DeleteMapping("/conges/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConge(@PathVariable Long id) {
        attendanceService.deleteConge(id);
        return ResponseEntity.noContent().build();
    }
}
