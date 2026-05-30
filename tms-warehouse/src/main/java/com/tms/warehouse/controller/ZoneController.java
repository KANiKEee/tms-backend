package com.tms.warehouse.controller;

import com.tms.warehouse.dto.ZoneRequest;
import com.tms.warehouse.dto.ZoneResponse;
import com.tms.warehouse.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrepots/{entrepotId}/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZoneResponse> create(@PathVariable Long entrepotId, @RequestBody ZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneService.create(entrepotId, request));
    }

    @GetMapping
    public ResponseEntity<List<ZoneResponse>> getByEntrepot(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(zoneService.getByEntrepot(entrepotId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneResponse> getById(@PathVariable Long entrepotId, @PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ZoneResponse> update(@PathVariable Long entrepotId, @PathVariable Long id, @RequestBody ZoneRequest request) {
        return ResponseEntity.ok(zoneService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long entrepotId, @PathVariable Long id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
