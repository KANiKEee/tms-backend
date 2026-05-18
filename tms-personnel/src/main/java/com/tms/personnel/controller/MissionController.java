package com.tms.personnel.controller;

import com.tms.personnel.dto.*;
import com.tms.personnel.service.MissionService;
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
    public ResponseEntity<MissionResponse> createMission(@RequestBody MissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.createMission(request));
    }

    @GetMapping
    public ResponseEntity<List<MissionResponse>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MissionResponse> updateMissionStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(missionService.updateMissionStatut(id, statut));
    }

    /**
     * Chauffeur-accessible endpoint to approve/start a mission after AI + GPS verification.
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','CHAUFFEUR')")
    public ResponseEntity<MissionResponse> approveMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.updateMissionStatut(id, "EN_COURS"));
    }

    /**
     * Chauffeur-accessible endpoint to complete a mission after destination GPS + photo verification.
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','CHAUFFEUR')")
    public ResponseEntity<MissionResponse> completeMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.updateMissionStatut(id, "TERMINEE"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}
