package com.tms.camion.service;

import com.tms.camion.dto.LocationResponse;
import com.tms.camion.dto.LocationUpdateRequest;
import com.tms.camion.entity.CamionLocationEntity;
import com.tms.camion.repository.CamionLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CamionLocationService {

    private final CamionLocationRepository locationRepository;

    @Transactional
    public LocationResponse updateLocation(LocationUpdateRequest request) {
        // Upsert: find existing row for this camion or create new
        CamionLocationEntity entity = locationRepository.findByCamionId(request.getCamionId())
                .orElse(CamionLocationEntity.builder()
                        .camionId(request.getCamionId())
                        .build());

        entity.setCamionImmatricule(request.getCamionImmatricule());
        entity.setChauffeurId(request.getChauffeurId());
        entity.setChauffeurNom(request.getChauffeurNom());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setSpeed(request.getSpeed());
        entity.setHeading(request.getHeading());
        entity.setTimestamp(LocalDateTime.now());

        return toResponse(locationRepository.save(entity));
    }

    public List<LocationResponse> getAllLatestLocations() {
        return locationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LocationResponse getLocationByCamionId(Long camionId) {
        CamionLocationEntity entity = locationRepository.findByCamionId(camionId)
                .orElseThrow(() -> new RuntimeException("Aucune localisation trouvée pour le camion: " + camionId));
        return toResponse(entity);
    }

    private LocationResponse toResponse(CamionLocationEntity e) {
        return LocationResponse.builder()
                .id(e.getId())
                .camionId(e.getCamionId())
                .camionImmatricule(e.getCamionImmatricule())
                .chauffeurId(e.getChauffeurId())
                .chauffeurNom(e.getChauffeurNom())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .speed(e.getSpeed())
                .heading(e.getHeading())
                .timestamp(e.getTimestamp())
                .build();
    }
}
