package com.tms.mission.controller;

import com.tms.mission.dto.*;
import com.tms.mission.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.createMission(request));
    }

    @GetMapping
    public ResponseEntity<List<MissionResponse>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    @GetMapping("/chauffeur/{chauffeurId}")
    public ResponseEntity<List<MissionResponse>> getMissionsByChauffeur(@PathVariable Long chauffeurId) {
        return ResponseEntity.ok(missionService.getMissionsByChauffeur(chauffeurId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<MissionResponse>> getMissionsByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(missionService.getMissionsByStatut(statut));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MissionResponse> updateMissionStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(missionService.updateMissionStatut(id, statut));
    }

    /**
     * Start a waiting mission after GPS, photo and AI verification in the mobile app.
     */
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN','CHAUFFEUR')")
    public ResponseEntity<MissionResponse> startMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.startMission(id));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','CHAUFFEUR')")
    public ResponseEntity<MissionResponse> approveMissionCompatibility(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.startMission(id));
    }

    /**
     * Chauffeur-accessible: complete a mission after destination GPS + photo verification.
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','CHAUFFEUR')")
    public ResponseEntity<MissionResponse> completeMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.completeMission(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}
