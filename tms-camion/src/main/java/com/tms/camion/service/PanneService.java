package com.tms.camion.service;

import com.tms.camion.dto.PanneRequest;
import com.tms.camion.dto.PanneResponse;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.entity.PanneEntity;
import com.tms.camion.repository.CamionRepository;
import com.tms.camion.repository.PanneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PanneService {

    private static final String DECLAREE = "DECLAREE";
    private static final String APPROUVEE = "APPROUVEE";
    private static final String EN_COURS = "EN_COURS";
    private static final String RESOLUE = "RESOLUE";
    private static final Set<String> ACTIVE_STATUSES = Set.of(APPROUVEE, EN_COURS);

    private final PanneRepository panneRepository;
    private final CamionRepository camionRepository;

    @Transactional
    public PanneResponse declarePanne(PanneRequest request) {
        CamionEntity camion = camionRepository.findById(request.getCamionId())
                .orElseThrow(() -> new RuntimeException("Camion non trouve avec l'ID: " + request.getCamionId()));

        PanneEntity panne = PanneEntity.builder()
                .camionId(camion.getId())
                .camionImmatricule(camion.getImmatricule())
                .chauffeurId(request.getChauffeurId())
                .chauffeurNom(normalizeText(request.getChauffeurNom(), "Administration"))
                .description(request.getDescription().trim())
                .localisation(normalizeText(request.getLocalisation(), null))
                .statut(DECLAREE)
                .build();

        return toResponse(panneRepository.save(panne));
    }

    public List<PanneResponse> getAllPannes() {
        return panneRepository.findAllByOrderByDateDeclarationDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PanneResponse> getPannesByStatut(String statut) {
        return panneRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .toList();
    }

    public long getUnresolvedCount() {
        return panneRepository.countByStatutNot(RESOLUE);
    }

    @Transactional
    public PanneResponse approvePanne(Long id) {
        PanneEntity panne = getPanne(id);
        if (!DECLAREE.equals(panne.getStatut())) {
            throw new RuntimeException("Seule une panne declaree peut etre approuvee");
        }

        panne.setStatut(APPROUVEE);
        markCamionAsBroken(panne.getCamionId());
        return toResponse(panneRepository.save(panne));
    }

    @Transactional
    public PanneResponse updateStatut(Long id, String statut) {
        PanneEntity panne = getPanne(id);
        String normalizedStatus = statut == null ? "" : statut.trim().toUpperCase();
        if (!EN_COURS.equals(normalizedStatus)) {
            throw new RuntimeException("Le statut autorise ici est EN_COURS");
        }
        if (!APPROUVEE.equals(panne.getStatut())) {
            throw new RuntimeException("La panne doit etre approuvee avant sa prise en charge");
        }

        panne.setStatut(EN_COURS);
        markCamionAsBroken(panne.getCamionId());
        return toResponse(panneRepository.save(panne));
    }

    @Transactional
    public PanneResponse resolvePanne(Long id, String resolution) {
        PanneEntity panne = getPanne(id);
        if (!ACTIVE_STATUSES.contains(panne.getStatut())) {
            throw new RuntimeException("La panne doit etre approuvee avant d'etre resolue");
        }

        panne.setStatut(RESOLUE);
        panne.setResolution(normalizeText(resolution, "Panne resolue"));
        panne.setDateResolution(LocalDateTime.now());

        PanneResponse response = toResponse(panneRepository.save(panne));
        restoreCamionIfNoActivePanne(panne.getCamionId(), panne.getId());
        return response;
    }

    @Transactional
    public void deletePanne(Long id) {
        PanneEntity panne = getPanne(id);
        boolean wasActive = ACTIVE_STATUSES.contains(panne.getStatut());
        panneRepository.delete(panne);
        if (wasActive) {
            restoreCamionIfNoActivePanne(panne.getCamionId(), panne.getId());
        }
    }

    private PanneEntity getPanne(Long id) {
        return panneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Panne non trouvee avec l'ID: " + id));
    }

    private void markCamionAsBroken(Long camionId) {
        CamionEntity camion = camionRepository.findById(camionId)
                .orElseThrow(() -> new RuntimeException("Camion non trouve avec l'ID: " + camionId));
        camion.setStatut("EN_PANNE");
        camionRepository.save(camion);
    }

    private void restoreCamionIfNoActivePanne(Long camionId, Long ignoredPanneId) {
        boolean hasAnotherActivePanne = panneRepository.findByCamionId(camionId).stream()
                .anyMatch(panne -> !panne.getId().equals(ignoredPanneId)
                        && ACTIVE_STATUSES.contains(panne.getStatut()));

        if (!hasAnotherActivePanne) {
            camionRepository.findById(camionId).ifPresent(camion -> {
                camion.setStatut("DISPONIBLE");
                camionRepository.save(camion);
            });
        }
    }

    private String normalizeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private PanneResponse toResponse(PanneEntity panne) {
        return PanneResponse.builder()
                .id(panne.getId())
                .camionId(panne.getCamionId())
                .camionImmatricule(panne.getCamionImmatricule())
                .chauffeurId(panne.getChauffeurId())
                .chauffeurNom(panne.getChauffeurNom())
                .description(panne.getDescription())
                .localisation(panne.getLocalisation())
                .statut(panne.getStatut())
                .resolution(panne.getResolution())
                .dateDeclaration(panne.getDateDeclaration())
                .dateResolution(panne.getDateResolution())
                .build();
    }
}
