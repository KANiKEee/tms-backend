package com.tms.personnel.repository;

import com.tms.personnel.entity.RetardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RetardRepository extends JpaRepository<RetardEntity, Long> {

    List<RetardEntity> findByPersonnelId(Long personnelId);

    @Query("SELECT r FROM RetardEntity r WHERE r.personnelId = :personnelId AND " +
           "r.date BETWEEN :start AND :end")
    List<RetardEntity> findByPersonnelIdAndDateRange(
            @Param("personnelId") Long personnelId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT r FROM RetardEntity r WHERE r.date BETWEEN :start AND :end")
    List<RetardEntity> findByDateRange(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    Page<RetardEntity> findByPersonnelId(Long personnelId, Pageable pageable);

    void deleteByPersonnelId(Long personnelId);
}
