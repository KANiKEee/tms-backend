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
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure ou egale a la date de debut");
        }

        Double volumeTotal = request.getVolumeTotal();
        if (request.getColisNombre() != null && request.getProduitVolumeUnitaire() != null) {
            volumeTotal = request.getColisNombre() * request.getProduitVolumeUnitaire();
        } else if (request.getColisNombre() != null && request.getColisHauteur() != null
                && request.getColisLargeur() != null && request.getColisLongueur() != null) {
            volumeTotal = request.getColisNombre() * request.getColisHauteur()
                    * request.getColisLargeur() * request.getColisLongueur();
        }

        Double poidsTotal = request.getPoidsTotal();
        if (request.getColisNombre() != null && request.getProduitPoidsUnitaire() != null) {
            poidsTotal = request.getColisNombre() * request.getProduitPoidsUnitaire();
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
                .produitId(request.getProduitId())
                .produitReference(request.getProduitReference())
                .produitNom(request.getProduitNom())
                .produitUnite(request.getProduitUnite())
                .produitVolumeUnitaire(request.getProduitVolumeUnitaire())
                .produitPoidsUnitaire(request.getProduitPoidsUnitaire())
                .poidsTotal(poidsTotal)
                .stockZoneId(request.getStockZoneId())
                .stockZoneNom(request.getStockZoneNom())
                .stockEntrepotId(request.getStockEntrepotId())
                .stockEntrepotNom(request.getStockEntrepotNom())
                .statut("EN_ATTENTE")
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

    @Transactional
    public MissionResponse startMission(Long id) {
        MissionEntity entity = findMission(id);
        if (!"EN_ATTENTE".equals(entity.getStatut())
                && !"PLANIFIEE".equals(entity.getStatut())) {
            throw new IllegalStateException("Seule une mission en attente peut etre demarree");
        }
        entity.setStatut("EN_COURS");
        return toResponse(missionRepository.save(entity));
    }

    @Transactional
    public MissionResponse completeMission(Long id) {
        MissionEntity entity = findMission(id);
        if (!"EN_COURS".equals(entity.getStatut())) {
            throw new IllegalStateException("La mission doit etre demarree avant d etre terminee");
        }
        entity.setStatut("TERMINEE");
        return toResponse(missionRepository.save(entity));
    }

    private MissionEntity findMission(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvee"));
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
                .produitId(e.getProduitId())
                .produitReference(e.getProduitReference())
                .produitNom(e.getProduitNom())
                .produitUnite(e.getProduitUnite())
                .produitVolumeUnitaire(e.getProduitVolumeUnitaire())
                .produitPoidsUnitaire(e.getProduitPoidsUnitaire())
                .poidsTotal(e.getPoidsTotal())
                .stockZoneId(e.getStockZoneId())
                .stockZoneNom(e.getStockZoneNom())
                .stockEntrepotId(e.getStockEntrepotId())
                .stockEntrepotNom(e.getStockEntrepotNom())
                .statut(e.getStatut())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
