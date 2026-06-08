package com.tms.warehouse.service;

import com.tms.warehouse.dto.*;
import com.tms.warehouse.entity.MouvementStockEntity;
import com.tms.warehouse.entity.ProduitEntity;
import com.tms.warehouse.entity.StockEntity;
import com.tms.warehouse.entity.ZoneEntity;
import com.tms.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final ZoneRepository zoneRepository;
    private final EntrepotRepository entrepotRepository;

    @Transactional
    public MouvementResponse createMouvement(MouvementRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (request.getMissionId() != null
                && "SORTIE".equals(request.getType())
                && mouvementRepository.existsByMissionIdAndProduitIdAndType(
                        request.getMissionId(), request.getProduitId(), request.getType())) {
            throw new RuntimeException("Une sortie de stock existe deja pour ce produit et cette mission");
        }

        // Validate product and zone exist
        produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + request.getProduitId()));
        zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone non trouvée avec l'ID: " + request.getZoneId()));

        switch (request.getType()) {
            case "ENTREE":
                handleEntree(request);
                break;
            case "SORTIE":
                handleSortie(request);
                break;
            case "TRANSFERT":
                handleTransfert(request);
                break;
            default:
                throw new RuntimeException("Type de mouvement invalide: " + request.getType());
        }

        MouvementStockEntity mouvement = MouvementStockEntity.builder()
                .produitId(request.getProduitId())
                .zoneId(request.getZoneId())
                .missionId(request.getMissionId())
                .type(request.getType())
                .quantite(request.getQuantite())
                .reference(request.getReference())
                .motif(request.getMotif())
                .effectuePar(username)
                .build();

        return toResponse(mouvementRepository.save(mouvement));
    }

    private void handleEntree(MouvementRequest request) {
        StockEntity stock = stockRepository.findByProduitIdAndZoneId(request.getProduitId(), request.getZoneId())
                .orElse(StockEntity.builder()
                        .produitId(request.getProduitId())
                        .zoneId(request.getZoneId())
                        .quantite(0)
                        .build());
        stock.setQuantite(stock.getQuantite() + request.getQuantite());
        stock.setDerniereMaj(LocalDateTime.now());
        stockRepository.save(stock);
    }

    private void handleSortie(MouvementRequest request) {
        StockEntity stock = stockRepository.findByProduitIdAndZoneId(request.getProduitId(), request.getZoneId())
                .orElseThrow(() -> new RuntimeException("Aucun stock trouvé pour ce produit dans cette zone"));
        if (stock.getQuantite() < request.getQuantite()) {
            throw new RuntimeException("Stock insuffisant. Disponible: " + stock.getQuantite() + ", Demandé: " + request.getQuantite());
        }
        stock.setQuantite(stock.getQuantite() - request.getQuantite());
        stock.setDerniereMaj(LocalDateTime.now());
        stockRepository.save(stock);
    }

    private void handleTransfert(MouvementRequest request) {
        if (request.getZoneDestinationId() == null) {
            throw new RuntimeException("Zone de destination requise pour un transfert");
        }
        zoneRepository.findById(request.getZoneDestinationId())
                .orElseThrow(() -> new RuntimeException("Zone de destination non trouvée avec l'ID: " + request.getZoneDestinationId()));

        // Remove from source
        handleSortie(request);

        // Add to destination
        StockEntity destStock = stockRepository.findByProduitIdAndZoneId(request.getProduitId(), request.getZoneDestinationId())
                .orElse(StockEntity.builder()
                        .produitId(request.getProduitId())
                        .zoneId(request.getZoneDestinationId())
                        .quantite(0)
                        .build());
        destStock.setQuantite(destStock.getQuantite() + request.getQuantite());
        destStock.setDerniereMaj(LocalDateTime.now());
        stockRepository.save(destStock);
    }

    public List<MouvementResponse> getAll() {
        return mouvementRepository.findAllByOrderByDateHeureDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MouvementResponse> getByProduit(Long produitId) {
        return mouvementRepository.findByProduitId(produitId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MouvementResponse> getByZone(Long zoneId) {
        return mouvementRepository.findByZoneId(zoneId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DashboardWarehouseResponse getDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        StockService stockService = new StockService(stockRepository, produitRepository, zoneRepository, entrepotRepository);
        List<StockResponse> alertes = stockService.getAlertes();

        List<MouvementResponse> derniers = mouvementRepository.findAllByOrderByDateHeureDesc().stream()
                .limit(10)
                .map(this::toResponse)
                .collect(Collectors.toList());

        return DashboardWarehouseResponse.builder()
                .totalEntrepots(entrepotRepository.count())
                .totalProduits(produitRepository.count())
                .totalZones(zoneRepository.count())
                .alertesStock(alertes.size())
                .mouvementsAujourdhui(mouvementRepository.countByDateHeureAfter(todayStart))
                .produitsEnAlerte(alertes)
                .derniersMouvements(derniers)
                .build();
    }

    private MouvementResponse toResponse(MouvementStockEntity m) {
        ProduitEntity produit = produitRepository.findById(m.getProduitId()).orElse(null);
        ZoneEntity zone = zoneRepository.findById(m.getZoneId()).orElse(null);

        return MouvementResponse.builder()
                .id(m.getId())
                .produitId(m.getProduitId())
                .produitNom(produit != null ? produit.getNom() : null)
                .produitReference(produit != null ? produit.getReference() : null)
                .zoneId(m.getZoneId())
                .zoneNom(zone != null ? zone.getNom() : null)
                .missionId(m.getMissionId())
                .type(m.getType())
                .quantite(m.getQuantite())
                .reference(m.getReference())
                .motif(m.getMotif())
                .effectuePar(m.getEffectuePar())
                .dateHeure(m.getDateHeure())
                .build();
    }
}
