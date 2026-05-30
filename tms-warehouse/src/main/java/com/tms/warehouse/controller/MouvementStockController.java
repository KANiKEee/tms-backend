package com.tms.warehouse.controller;

import com.tms.warehouse.dto.DashboardWarehouseResponse;
import com.tms.warehouse.dto.MouvementRequest;
import com.tms.warehouse.dto.MouvementResponse;
import com.tms.warehouse.service.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MouvementResponse> create(@RequestBody MouvementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mouvementService.createMouvement(request));
    }

    @GetMapping
    public ResponseEntity<List<MouvementResponse>> getAll() {
        return ResponseEntity.ok(mouvementService.getAll());
    }

    @GetMapping("/produit/{produitId}")
    public ResponseEntity<List<MouvementResponse>> getByProduit(@PathVariable Long produitId) {
        return ResponseEntity.ok(mouvementService.getByProduit(produitId));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<MouvementResponse>> getByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(mouvementService.getByZone(zoneId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardWarehouseResponse> getDashboard() {
        return ResponseEntity.ok(mouvementService.getDashboard());
    }
}
