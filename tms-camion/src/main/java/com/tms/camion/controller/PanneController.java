package com.tms.camion.controller;

import com.tms.camion.dto.PanneRequest;
import com.tms.camion.dto.PanneResponse;
import com.tms.camion.service.PanneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pannes")
@RequiredArgsConstructor
public class PanneController {

    private final PanneService panneService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHAUFFEUR')")
    public ResponseEntity<PanneResponse> declarePanne(
            @Valid @RequestBody PanneRequest request,
            Authentication authentication
    ) {
        request.setChauffeurNom(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(panneService.declarePanne(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PanneResponse>> getAllPannes() {
        return ResponseEntity.ok(panneService.getAllPannes());
    }

    @GetMapping("/unresolved-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnresolvedCount() {
        return ResponseEntity.ok(Map.of("count", panneService.getUnresolvedCount()));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PanneResponse> approvePanne(@PathVariable Long id) {
        return ResponseEntity.ok(panneService.approvePanne(id));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PanneResponse> resolvePanne(@PathVariable Long id, @RequestParam String resolution) {
        return ResponseEntity.ok(panneService.resolvePanne(id, resolution));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PanneResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(panneService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePanne(@PathVariable Long id) {
        panneService.deletePanne(id);
        return ResponseEntity.noContent().build();
    }
}
