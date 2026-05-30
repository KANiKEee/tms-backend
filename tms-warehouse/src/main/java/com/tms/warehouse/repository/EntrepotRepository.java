package com.tms.warehouse.repository;

import com.tms.warehouse.entity.EntrepotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrepotRepository extends JpaRepository<EntrepotEntity, Long> {

    List<EntrepotEntity> findByStatut(String statut);

    List<EntrepotEntity> findByVille(String ville);

    boolean existsByNom(String nom);
}
