package com.tms.camion.controller;

import com.tms.camion.dto.CamionRequest;
import com.tms.camion.dto.CamionResponse;
import com.tms.camion.service.CamionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camions")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CamionResponse> createCamion(@RequestBody CamionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(camionService.createCamion(request));
    }

    @GetMapping
    public ResponseEntity<List<CamionResponse>> getAllCamions() {
        return ResponseEntity.ok(camionService.getAllCamions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CamionResponse> getCamionById(@PathVariable Long id) {
        return ResponseEntity.ok(camionService.getCamionById(id));
    }

    @GetMapping("/immatricule/{immatricule}")
    public ResponseEntity<CamionResponse> getCamionByImmatricule(@PathVariable String immatricule) {
        return ResponseEntity.ok(camionService.getCamionByImmatricule(immatricule));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<CamionResponse>> getCamionsByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(camionService.getCamionsByStatut(statut));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CamionResponse> updateCamion(@PathVariable Long id, @RequestBody CamionRequest request) {
        return ResponseEntity.ok(camionService.updateCamion(id, request));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CamionResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(camionService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCamion(@PathVariable Long id) {
        camionService.deleteCamion(id);
        return ResponseEntity.noContent().build();
    }
}
