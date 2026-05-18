package com.tms.personnel.service;

import com.tms.personnel.dto.*;
import com.tms.personnel.entity.*;
import com.tms.personnel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final PersonnelRepository personnelRepository;

    @Transactional
    public MissionResponse createMission(MissionRequest request) {
        // Calculate volume: nombre * hauteur * largeur * longueur
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
                .statut(request.getStatut() != null ? request.getStatut() : "EN_COURS")
                .build();
        return toMissionResponse(missionRepository.save(entity));
    }

    public List<MissionResponse> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::toMissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MissionResponse updateMissionStatut(Long id, String statut) {
        MissionEntity entity = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));
        entity.setStatut(statut);
        return toMissionResponse(missionRepository.save(entity));
    }

    @Transactional
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    public MissionResponse toMissionResponse(MissionEntity e) {
        PersonnelEntity p = personnelRepository.findById(e.getChauffeurId()).orElse(null);
        return MissionResponse.builder()
                .id(e.getId())
                .chauffeurId(e.getChauffeurId())
                .chauffeurNom(p != null ? p.getNom() : "Inconnu")
                .chauffeurPrenom(p != null ? p.getPrenom() : "")
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
