package com.tms.camion.repository;

import com.tms.camion.entity.CamionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CamionRepository extends JpaRepository<CamionEntity, Long> {

    Optional<CamionEntity> findByImmatricule(String immatricule);

    List<CamionEntity> findByStatut(String statut);

    List<CamionEntity> findByMarque(String marque);

    List<CamionEntity> findByNature(String nature);

    List<CamionEntity> findByType(String type);

    boolean existsByImmatricule(String immatricule);
}
