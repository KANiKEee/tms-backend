package com.tms.warehouse.service;

import com.tms.warehouse.dto.ProduitRequest;
import com.tms.warehouse.dto.ProduitResponse;
import com.tms.warehouse.entity.ProduitEntity;
import com.tms.warehouse.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;

    @Transactional
    public ProduitResponse create(ProduitRequest request) {
        if (produitRepository.existsByReference(request.getReference())) {
            throw new RuntimeException("Un produit avec la référence '" + request.getReference() + "' existe déjà");
        }
        ProduitEntity entity = ProduitEntity.builder()
                .reference(request.getReference())
                .nom(request.getNom())
                .description(request.getDescription())
                .categorie(request.getCategorie())
                .unite(request.getUnite())
                .poidsUnitaire(request.getPoidsUnitaire())
                .volumeUnitaire(request.getVolumeUnitaire())
                .seuilAlerte(request.getSeuilAlerte())
                .build();
        return toResponse(produitRepository.save(entity));
    }

    public List<ProduitResponse> getAll() {
        return produitRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProduitResponse getById(Long id) {
        ProduitEntity entity = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));
        return toResponse(entity);
    }

    public ProduitResponse getByReference(String reference) {
        ProduitEntity entity = produitRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec la référence: " + reference));
        return toResponse(entity);
    }

    public List<ProduitResponse> getByCategorie(String categorie) {
        return produitRepository.findByCategorie(categorie).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProduitResponse update(Long id, ProduitRequest request) {
        ProduitEntity entity = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        if (request.getReference() != null) entity.setReference(request.getReference());
        if (request.getNom() != null) entity.setNom(request.getNom());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getCategorie() != null) entity.setCategorie(request.getCategorie());
        if (request.getUnite() != null) entity.setUnite(request.getUnite());
        if (request.getPoidsUnitaire() != null) entity.setPoidsUnitaire(request.getPoidsUnitaire());
        if (request.getVolumeUnitaire() != null) entity.setVolumeUnitaire(request.getVolumeUnitaire());
        if (request.getSeuilAlerte() != null) entity.setSeuilAlerte(request.getSeuilAlerte());

        return toResponse(produitRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }
        produitRepository.deleteById(id);
    }

    private ProduitResponse toResponse(ProduitEntity e) {
        return ProduitResponse.builder()
                .id(e.getId())
                .reference(e.getReference())
                .nom(e.getNom())
                .description(e.getDescription())
                .categorie(e.getCategorie())
                .unite(e.getUnite())
                .poidsUnitaire(e.getPoidsUnitaire())
                .volumeUnitaire(e.getVolumeUnitaire())
                .seuilAlerte(e.getSeuilAlerte())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
