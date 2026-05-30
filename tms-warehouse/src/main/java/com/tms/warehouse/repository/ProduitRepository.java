package com.tms.warehouse.repository;

import com.tms.warehouse.entity.ProduitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<ProduitEntity, Long> {

    Optional<ProduitEntity> findByReference(String reference);

    List<ProduitEntity> findByCategorie(String categorie);

    boolean existsByReference(String reference);
}
