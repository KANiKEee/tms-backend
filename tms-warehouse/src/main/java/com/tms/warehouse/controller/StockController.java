package com.tms.warehouse.controller;

import com.tms.warehouse.dto.StockResponse;
import com.tms.warehouse.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAll() {
        return ResponseEntity.ok(stockService.getAll());
    }

    @GetMapping("/entrepot/{entrepotId}")
    public ResponseEntity<List<StockResponse>> getByEntrepot(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(stockService.getByEntrepot(entrepotId));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<StockResponse>> getByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(stockService.getByZone(zoneId));
    }

    @GetMapping("/alertes")
    public ResponseEntity<List<StockResponse>> getAlertes() {
        return ResponseEntity.ok(stockService.getAlertes());
    }
}
