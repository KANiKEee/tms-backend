package com.tms.warehouse.repository;

import com.tms.warehouse.entity.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<ZoneEntity, Long> {

    List<ZoneEntity> findByEntrepotId(Long entrepotId);

    List<ZoneEntity> findByEntrepotIdAndStatut(Long entrepotId, String statut);

    List<ZoneEntity> findByType(String type);
}
