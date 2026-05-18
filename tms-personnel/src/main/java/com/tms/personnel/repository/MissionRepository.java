package com.tms.personnel.repository;

import com.tms.personnel.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepository extends JpaRepository<MissionEntity, Long> {

    List<MissionEntity> findByChauffeurId(Long chauffeurId);

    List<MissionEntity> findByStatut(String statut);

    @Query("SELECT m FROM MissionEntity m WHERE m.dateDebut <= :end AND m.dateFin >= :start")
    List<MissionEntity> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    void deleteByChauffeurId(Long chauffeurId);
}
