package com.tms.warehouse.service;

import com.tms.warehouse.dto.EntrepotRequest;
import com.tms.warehouse.dto.EntrepotResponse;
import com.tms.warehouse.entity.EntrepotEntity;
import com.tms.warehouse.repository.EntrepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrepotService {

    private final EntrepotRepository entrepotRepository;

    @Transactional
    public EntrepotResponse create(EntrepotRequest request) {
        EntrepotEntity entity = EntrepotEntity.builder()
                .nom(request.getNom())
                .adresse(request.getAdresse())
                .ville(request.getVille())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .telephone(request.getTelephone())
                .responsable(request.getResponsable())
                .capaciteTotale(request.getCapaciteTotale())
                .statut(request.getStatut() != null ? request.getStatut() : "ACTIF")
                .build();
        return toResponse(entrepotRepository.save(entity));
    }

    public List<EntrepotResponse> getAll() {
        return entrepotRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EntrepotResponse getById(Long id) {
        EntrepotEntity entity = entrepotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));
        return toResponse(entity);
    }

    public List<EntrepotResponse> getByStatut(String statut) {
        return entrepotRepository.findByStatut(statut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EntrepotResponse update(Long id, EntrepotRequest request) {
        EntrepotEntity entity = entrepotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID: " + id));

        if (request.getNom() != null) entity.setNom(request.getNom());
        if (request.getAdresse() != null) entity.setAdresse(request.getAdresse());
        if (request.getVille() != null) entity.setVille(request.getVille());
        if (request.getLatitude() != null) entity.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) entity.setLongitude(request.getLongitude());
        if (request.getTelephone() != null) entity.setTelephone(request.getTelephone());
        if (request.getResponsable() != null) entity.setResponsable(request.getResponsable());
        if (request.getCapaciteTotale() != null) entity.setCapaciteTotale(request.getCapaciteTotale());
        if (request.getStatut() != null) entity.setStatut(request.getStatut());

        return toResponse(entrepotRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!entrepotRepository.existsById(id)) {
            throw new RuntimeException("Entrepôt non trouvé avec l'ID: " + id);
        }
        entrepotRepository.deleteById(id);
    }

    private EntrepotResponse toResponse(EntrepotEntity e) {
        return EntrepotResponse.builder()
                .id(e.getId())
                .nom(e.getNom())
                .adresse(e.getAdresse())
                .ville(e.getVille())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .telephone(e.getTelephone())
                .responsable(e.getResponsable())
                .capaciteTotale(e.getCapaciteTotale())
                .statut(e.getStatut())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
