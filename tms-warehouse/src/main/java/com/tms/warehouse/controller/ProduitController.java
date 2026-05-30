package com.tms.warehouse.controller;

import com.tms.warehouse.dto.ProduitRequest;
import com.tms.warehouse.dto.ProduitResponse;
import com.tms.warehouse.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitResponse> create(@RequestBody ProduitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProduitResponse>> getAll() {
        return ResponseEntity.ok(produitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.getById(id));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<ProduitResponse> getByReference(@PathVariable String reference) {
        return ResponseEntity.ok(produitService.getByReference(reference));
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<ProduitResponse>> getByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(produitService.getByCategorie(categorie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProduitResponse> update(@PathVariable Long id, @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(produitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
