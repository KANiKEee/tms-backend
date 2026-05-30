package com.tms.warehouse.service;

import com.tms.warehouse.dto.StockResponse;
import com.tms.warehouse.entity.EntrepotEntity;
import com.tms.warehouse.entity.ProduitEntity;
import com.tms.warehouse.entity.StockEntity;
import com.tms.warehouse.entity.ZoneEntity;
import com.tms.warehouse.repository.EntrepotRepository;
import com.tms.warehouse.repository.ProduitRepository;
import com.tms.warehouse.repository.StockRepository;
import com.tms.warehouse.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final ZoneRepository zoneRepository;
    private final EntrepotRepository entrepotRepository;

    public List<StockResponse> getAll() {
        return stockRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockResponse> getByEntrepot(Long entrepotId) {
        List<Long> zoneIds = zoneRepository.findByEntrepotId(entrepotId).stream()
                .map(ZoneEntity::getId)
                .collect(Collectors.toList());
        if (zoneIds.isEmpty()) return List.of();
        return stockRepository.findByZoneIdIn(zoneIds).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockResponse> getByZone(Long zoneId) {
        return stockRepository.findByZoneId(zoneId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StockResponse> getAlertes() {
        return stockRepository.findAll().stream()
                .map(this::toResponse)
                .filter(StockResponse::isEnAlerte)
                .collect(Collectors.toList());
    }

    private StockResponse toResponse(StockEntity s) {
        ProduitEntity produit = produitRepository.findById(s.getProduitId()).orElse(null);
        ZoneEntity zone = zoneRepository.findById(s.getZoneId()).orElse(null);
        EntrepotEntity entrepot = zone != null ? entrepotRepository.findById(zone.getEntrepotId()).orElse(null) : null;

        Integer seuil = produit != null ? produit.getSeuilAlerte() : null;
        boolean enAlerte = seuil != null && s.getQuantite() <= seuil;

        return StockResponse.builder()
                .id(s.getId())
                .produitId(s.getProduitId())
                .produitNom(produit != null ? produit.getNom() : null)
                .produitReference(produit != null ? produit.getReference() : null)
                .zoneId(s.getZoneId())
                .zoneNom(zone != null ? zone.getNom() : null)
                .entrepotId(entrepot != null ? entrepot.getId() : null)
                .entrepotNom(entrepot != null ? entrepot.getNom() : null)
                .quantite(s.getQuantite())
                .seuilAlerte(seuil)
                .enAlerte(enAlerte)
                .derniereMaj(s.getDerniereMaj())
                .build();
    }
}
