package com.tms.camion.service;

import com.tms.camion.dto.LocationResponse;
import com.tms.camion.dto.LocationUpdateRequest;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.entity.CamionLocationEntity;
import com.tms.camion.repository.CamionRepository;
import com.tms.camion.repository.CamionLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CamionLocationService {

    private final CamionLocationRepository locationRepository;
    private final CamionRepository camionRepository;

    @Transactional
    public LocationResponse updateLocation(LocationUpdateRequest request) {
        CamionEntity camion = camionRepository.findById(request.getCamionId())
                .orElseThrow(() -> new RuntimeException(
                        "Camion non trouve avec l'ID: " + request.getCamionId()));

        CamionLocationEntity entity = locationRepository.findByCamionId(request.getCamionId())
                .orElse(CamionLocationEntity.builder()
                        .camionId(request.getCamionId())
                        .build());

        entity.setMissionId(request.getMissionId());
        entity.setCamionImmatricule(camion.getImmatricule());
        entity.setChauffeurId(request.getChauffeurId());
        entity.setChauffeurNom(getAuthenticatedUsername());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setAccuracy(request.getAccuracy());
        entity.setSpeed(request.getSpeed());
        entity.setHeading(request.getHeading());
        entity.setTimestamp(LocalDateTime.now());

        return toResponse(locationRepository.save(entity));
    }

    public List<LocationResponse> getAllLatestLocations() {
        return locationRepository
                .findByTimestampAfterOrderByTimestampDesc(LocalDateTime.now().minusMinutes(2))
                .stream()
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
                .missionId(e.getMissionId())
                .camionId(e.getCamionId())
                .camionImmatricule(e.getCamionImmatricule())
                .chauffeurId(e.getChauffeurId())
                .chauffeurNom(e.getChauffeurNom())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .accuracy(e.getAccuracy())
                .speed(e.getSpeed())
                .heading(e.getHeading())
                .timestamp(e.getTimestamp())
                .build();
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : "Inconnu";
    }
}
