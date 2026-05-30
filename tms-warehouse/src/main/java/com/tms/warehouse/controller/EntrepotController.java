package com.tms.warehouse.controller;

import com.tms.warehouse.dto.EntrepotRequest;
import com.tms.warehouse.dto.EntrepotResponse;
import com.tms.warehouse.service.EntrepotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrepots")
@RequiredArgsConstructor
public class EntrepotController {

    private final EntrepotService entrepotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntrepotResponse> create(@RequestBody EntrepotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrepotService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EntrepotResponse>> getAll() {
        return ResponseEntity.ok(entrepotService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepotResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrepotService.getById(id));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<EntrepotResponse>> getByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(entrepotService.getByStatut(statut));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntrepotResponse> update(@PathVariable Long id, @RequestBody EntrepotRequest request) {
        return ResponseEntity.ok(entrepotService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        entrepotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
