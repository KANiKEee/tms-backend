package com.tms.personnel.repository;

import com.tms.personnel.entity.AbsenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<AbsenceEntity, Long> {

    List<AbsenceEntity> findByPersonnelId(Long personnelId);

    @Query("SELECT a FROM AbsenceEntity a WHERE a.personnelId = :personnelId AND " +
           "((a.dateDebut <= :end AND a.dateFin >= :start))")
    List<AbsenceEntity> findByPersonnelIdAndDateRange(
            @Param("personnelId") Long personnelId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT a FROM AbsenceEntity a WHERE " +
           "(a.dateDebut <= :end AND a.dateFin >= :start)")
    List<AbsenceEntity> findByDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    Page<AbsenceEntity> findByPersonnelId(Long personnelId, Pageable pageable);

    void deleteByPersonnelId(Long personnelId);
}
