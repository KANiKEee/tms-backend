package com.tms.camion.service;

import com.tms.camion.dto.CamionRequest;
import com.tms.camion.dto.CamionResponse;
import com.tms.camion.entity.CamionEntity;
import com.tms.camion.repository.CamionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CamionService {

    private final CamionRepository camionRepository;

    @Transactional
    public CamionResponse createCamion(CamionRequest request) {
        if (camionRepository.existsByImmatricule(request.getImmatricule())) {
            throw new RuntimeException("Un camion avec l'immatriculation '" + request.getImmatricule() + "' existe déjà");
        }
        CamionEntity entity = CamionEntity.builder()
                .immatricule(request.getImmatricule())
                .marque(request.getMarque())
                .modele(request.getModele())
                .nature(request.getNature())
                .type(request.getType())
                .volume(request.getVolume())
                .poidsMax(request.getPoidsMax())
                .carburant(request.getCarburant())
                .annee(request.getAnnee())
                .kilometrage(request.getKilometrage())
                .statut(request.getStatut() != null ? request.getStatut() : "DISPONIBLE")
                .dateAchat(request.getDateAchat())
                .dateProchainCT(request.getDateProchainCT())
                .assuranceExpiration(request.getAssuranceExpiration())
                .build();
        return toResponse(camionRepository.save(entity));
    }

    public List<CamionResponse> getAllCamions() {
        return camionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CamionResponse getCamionById(Long id) {
        CamionEntity entity = camionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camion non trouvé avec l'ID: " + id));
        return toResponse(entity);
    }

    public CamionResponse getCamionByImmatricule(String immatricule) {
        CamionEntity entity = camionRepository.findByImmatricule(immatricule)
                .orElseThrow(() -> new RuntimeException("Camion non trouvé avec l'immatriculation: " + immatricule));
        return toResponse(entity);
    }

    public List<CamionResponse> getCamionsByStatut(String statut) {
        return camionRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CamionResponse updateCamion(Long id, CamionRequest request) {
        CamionEntity entity = camionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camion non trouvé avec l'ID: " + id));

        if (request.getImmatricule() != null) entity.setImmatricule(request.getImmatricule());
        if (request.getMarque() != null) entity.setMarque(request.getMarque());
        if (request.getModele() != null) entity.setModele(request.getModele());
        if (request.getNature() != null) entity.setNature(request.getNature());
        if (request.getType() != null) entity.setType(request.getType());
        if (request.getVolume() != null) entity.setVolume(request.getVolume());
        if (request.getPoidsMax() != null) entity.setPoidsMax(request.getPoidsMax());
        if (request.getCarburant() != null) entity.setCarburant(request.getCarburant());
        if (request.getAnnee() != null) entity.setAnnee(request.getAnnee());
        if (request.getKilometrage() != null) entity.setKilometrage(request.getKilometrage());
        if (request.getStatut() != null) entity.setStatut(request.getStatut());
        if (request.getDateAchat() != null) entity.setDateAchat(request.getDateAchat());
        if (request.getDateProchainCT() != null) entity.setDateProchainCT(request.getDateProchainCT());
        if (request.getAssuranceExpiration() != null) entity.setAssuranceExpiration(request.getAssuranceExpiration());

        return toResponse(camionRepository.save(entity));
    }

    @Transactional
    public CamionResponse updateStatut(Long id, String statut) {
        CamionEntity entity = camionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camion non trouvé avec l'ID: " + id));
        entity.setStatut(statut);
        return toResponse(camionRepository.save(entity));
    }

    @Transactional
    public void deleteCamion(Long id) {
        if (!camionRepository.existsById(id)) {
            throw new RuntimeException("Camion non trouvé avec l'ID: " + id);
        }
        camionRepository.deleteById(id);
    }

    private CamionResponse toResponse(CamionEntity e) {
        return CamionResponse.builder()
                .id(e.getId())
                .immatricule(e.getImmatricule())
                .marque(e.getMarque())
                .modele(e.getModele())
                .nature(e.getNature())
                .type(e.getType())
                .volume(e.getVolume())
                .poidsMax(e.getPoidsMax())
                .carburant(e.getCarburant())
                .annee(e.getAnnee())
                .kilometrage(e.getKilometrage())
                .statut(e.getStatut())
                .dateAchat(e.getDateAchat())
                .dateProchainCT(e.getDateProchainCT())
                .assuranceExpiration(e.getAssuranceExpiration())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
