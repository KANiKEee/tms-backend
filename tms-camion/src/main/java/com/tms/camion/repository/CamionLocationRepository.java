package com.tms.camion.repository;

import com.tms.camion.entity.CamionLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CamionLocationRepository extends JpaRepository<CamionLocationEntity, Long> {

    Optional<CamionLocationEntity> findByCamionId(Long camionId);

    List<CamionLocationEntity> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);
}
