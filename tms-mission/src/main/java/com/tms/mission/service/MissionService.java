package com.tms.mission.service;

import com.tms.mission.dto.*;
import com.tms.mission.entity.*;
import com.tms.mission.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    @Transactional
    public MissionResponse createMission(MissionRequest request) {
        Double volumeTotal = null;
        if (request.getColisNombre() != null && request.getColisHauteur() != null
                && request.getColisLargeur() != null && request.getColisLongueur() != null) {
            volumeTotal = request.getColisNombre() * request.getColisHauteur()
                    * request.getColisLargeur() * request.getColisLongueur();
        } else if (request.getVolumeTotal() != null) {
            volumeTotal = request.getVolumeTotal();
        }

        MissionEntity entity = MissionEntity.builder()
                .chauffeurId(request.getChauffeurId())
                .camionId(request.getCamionId())
                .depart(request.getDepart())
                .departLat(request.getDepartLat())
                .departLng(request.getDepartLng())
                .destination(request.getDestination())
                .destinationLat(request.getDestinationLat())
                .destinationLng(request.getDestinationLng())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .description(request.getDescription())
                .colisNombre(request.getColisNombre())
                .colisHauteur(request.getColisHauteur())
                .colisLargeur(request.getColisLargeur())
                .colisLongueur(request.getColisLongueur())
                .volumeTotal(volumeTotal)
                .statut(request.getStatut() != null ? request.getStatut() : "PLANIFIEE")
                .build();
        return toResponse(missionRepository.save(entity));
    }

    public List<MissionResponse> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MissionResponse> getMissionsByChauffeur(Long chauffeurId) {
        return missionRepository.findByChauffeurId(chauffeurId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MissionResponse> getMissionsByStatut(String statut) {
        return missionRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MissionResponse updateMissionStatut(Long id, String statut) {
        MissionEntity entity = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        entity.setStatut(statut);
        return toResponse(missionRepository.save(entity));
    }

    @Transactional
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    private MissionResponse toResponse(MissionEntity e) {
        return MissionResponse.builder()
                .id(e.getId())
                .chauffeurId(e.getChauffeurId())
                .camionId(e.getCamionId())
                .depart(e.getDepart())
                .departLat(e.getDepartLat())
                .departLng(e.getDepartLng())
                .destination(e.getDestination())
                .destinationLat(e.getDestinationLat())
                .destinationLng(e.getDestinationLng())
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .description(e.getDescription())
                .colisNombre(e.getColisNombre())
                .colisHauteur(e.getColisHauteur())
                .colisLargeur(e.getColisLargeur())
                .colisLongueur(e.getColisLongueur())
                .volumeTotal(e.getVolumeTotal())
                .statut(e.getStatut())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
