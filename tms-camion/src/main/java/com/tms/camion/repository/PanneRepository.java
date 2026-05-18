package com.tms.camion.repository;

import com.tms.camion.entity.PanneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PanneRepository extends JpaRepository<PanneEntity, Long> {

    List<PanneEntity> findByStatut(String statut);

    List<PanneEntity> findByCamionId(Long camionId);

    List<PanneEntity> findByChauffeurId(Long chauffeurId);

    long countByStatutNot(String statut);

    List<PanneEntity> findAllByOrderByDateDeclarationDesc();
}
