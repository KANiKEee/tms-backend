package com.tms.camion.controller;

import com.tms.camion.dto.LocationResponse;
import com.tms.camion.dto.LocationUpdateRequest;
import com.tms.camion.service.CamionLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/camions/locations")
@RequiredArgsConstructor
public class CamionLocationController {

    private final CamionLocationService locationService;

    /**
     * Receive a GPS location update from the chauffeur mobile app.
     */
    @PostMapping
    public ResponseEntity<LocationResponse> updateLocation(@RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(locationService.updateLocation(request));
    }

    /**
     * Get the latest known location for all camions (for the admin tracking map).
     */
    @GetMapping("/latest")
    public ResponseEntity<List<LocationResponse>> getLatestLocations() {
        return ResponseEntity.ok(locationService.getAllLatestLocations());
    }

    /**
     * Get the latest location for a specific camion.
     */
    @GetMapping("/{camionId}")
    public ResponseEntity<LocationResponse> getLocationByCamionId(@PathVariable Long camionId) {
        return ResponseEntity.ok(locationService.getLocationByCamionId(camionId));
    }
}
