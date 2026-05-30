package com.tms.warehouse.repository;

import com.tms.warehouse.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findByProduitIdAndZoneId(Long produitId, Long zoneId);

    List<StockEntity> findByZoneId(Long zoneId);

    List<StockEntity> findByProduitId(Long produitId);

    @Query("SELECT s FROM StockEntity s WHERE s.zoneId IN :zoneIds")
    List<StockEntity> findByZoneIdIn(@Param("zoneIds") List<Long> zoneIds);
}
