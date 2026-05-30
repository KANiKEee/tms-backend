package com.tms.warehouse.service;

import com.tms.warehouse.dto.ZoneRequest;
import com.tms.warehouse.dto.ZoneResponse;
import com.tms.warehouse.entity.ZoneEntity;
import com.tms.warehouse.repository.EntrepotRepository;
import com.tms.warehouse.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final EntrepotRepository entrepotRepository;

    @Transactional
    public ZoneResponse create(Long entrepotId, ZoneRequest request) {
        if (!entrepotRepository.existsById(entrepotId)) {
            throw new RuntimeException("Entrepôt non trouvé avec l'ID: " + entrepotId);
        }
        ZoneEntity entity = ZoneEntity.builder()
                .entrepotId(entrepotId)
                .nom(request.getNom())
                .type(request.getType())
                .capacite(request.getCapacite())
                .statut(request.getStatut() != null ? request.getStatut() : "DISPONIBLE")
                .build();
        return toResponse(zoneRepository.save(entity));
    }

    public List<ZoneResponse> getByEntrepot(Long entrepotId) {
        return zoneRepository.findByEntrepotId(entrepotId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ZoneResponse getById(Long id) {
        ZoneEntity entity = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée avec l'ID: " + id));
        return toResponse(entity);
    }

    @Transactional
    public ZoneResponse update(Long id, ZoneRequest request) {
        ZoneEntity entity = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée avec l'ID: " + id));

        if (request.getNom() != null) entity.setNom(request.getNom());
        if (request.getType() != null) entity.setType(request.getType());
        if (request.getCapacite() != null) entity.setCapacite(request.getCapacite());
        if (request.getStatut() != null) entity.setStatut(request.getStatut());

        return toResponse(zoneRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("Zone non trouvée avec l'ID: " + id);
        }
        zoneRepository.deleteById(id);
    }

    private ZoneResponse toResponse(ZoneEntity e) {
        return ZoneResponse.builder()
                .id(e.getId())
                .entrepotId(e.getEntrepotId())
                .nom(e.getNom())
                .type(e.getType())
                .capacite(e.getCapacite())
                .statut(e.getStatut())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
