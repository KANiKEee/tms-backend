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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PanneService {

    private final PanneRepository panneRepository;
    private final CamionRepository camionRepository;

    @Transactional
    public PanneResponse declarePanne(PanneRequest request) {
        CamionEntity camion = camionRepository.findById(request.getCamionId())
                .orElseThrow(() -> new RuntimeException("Camion non trouvé avec l'ID: " + request.getCamionId()));

        // Auto-set camion to EN_PANNE
        camion.setStatut("EN_PANNE");
        camionRepository.save(camion);

        PanneEntity panne = PanneEntity.builder()
                .camionId(camion.getId())
                .camionImmatricule(camion.getImmatricule())
                .chauffeurId(request.getChauffeurId())
                .chauffeurNom(request.getChauffeurNom())
                .description(request.getDescription())
                .localisation(request.getLocalisation())
                .statut("DECLAREE")
                .build();

        return toResponse(panneRepository.save(panne));
    }

    public List<PanneResponse> getAllPannes() {
        return panneRepository.findAllByOrderByDateDeclarationDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PanneResponse> getPannesByStatut(String statut) {
        return panneRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long getUnresolvedCount() {
        return panneRepository.countByStatutNot("RESOLUE");
    }

    @Transactional
    public PanneResponse resolvePanne(Long id, String resolution) {
        PanneEntity panne = panneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Panne non trouvée avec l'ID: " + id));

        panne.setStatut("RESOLUE");
        panne.setResolution(resolution);
        panne.setDateResolution(LocalDateTime.now());

        // Set camion back to DISPONIBLE
        CamionEntity camion = camionRepository.findById(panne.getCamionId()).orElse(null);
        if (camion != null) {
            camion.setStatut("DISPONIBLE");
            camionRepository.save(camion);
        }

        return toResponse(panneRepository.save(panne));
    }

    @Transactional
    public PanneResponse updateStatut(Long id, String statut) {
        PanneEntity panne = panneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Panne non trouvée avec l'ID: " + id));
        panne.setStatut(statut);
        return toResponse(panneRepository.save(panne));
    }

    private PanneResponse toResponse(PanneEntity e) {
        return PanneResponse.builder()
                .id(e.getId())
                .camionId(e.getCamionId())
                .camionImmatricule(e.getCamionImmatricule())
                .chauffeurId(e.getChauffeurId())
                .chauffeurNom(e.getChauffeurNom())
                .description(e.getDescription())
                .localisation(e.getLocalisation())
                .statut(e.getStatut())
                .resolution(e.getResolution())
                .dateDeclaration(e.getDateDeclaration())
                .dateResolution(e.getDateResolution())
                .build();
    }
}
