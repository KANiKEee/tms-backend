package com.tms.personnel.repository;

import com.tms.personnel.entity.CongeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CongeRepository extends JpaRepository<CongeEntity, Long> {

    List<CongeEntity> findByPersonnelId(Long personnelId);

    @Query("SELECT c FROM CongeEntity c WHERE c.personnelId = :personnelId AND " +
           "((c.dateDebut <= :end AND c.dateFin >= :start))")
    List<CongeEntity> findByPersonnelIdAndDateRange(
            @Param("personnelId") Long personnelId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT c FROM CongeEntity c WHERE " +
           "(c.dateDebut <= :end AND c.dateFin >= :start)")
    List<CongeEntity> findByDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    Page<CongeEntity> findByPersonnelId(Long personnelId, Pageable pageable);

    List<CongeEntity> findByStatut(String statut);

    void deleteByPersonnelId(Long personnelId);
}
