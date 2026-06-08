package com.tms.warehouse.repository;

import com.tms.warehouse.entity.MouvementStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStockEntity, Long> {

    List<MouvementStockEntity> findByProduitId(Long produitId);

    List<MouvementStockEntity> findByZoneId(Long zoneId);

    List<MouvementStockEntity> findByMissionId(Long missionId);

    boolean existsByMissionIdAndProduitIdAndType(Long missionId, Long produitId, String type);

    List<MouvementStockEntity> findByType(String type);

    List<MouvementStockEntity> findByDateHeureAfter(LocalDateTime dateTime);

    List<MouvementStockEntity> findAllByOrderByDateHeureDesc();

    long countByDateHeureAfter(LocalDateTime dateTime);
}
