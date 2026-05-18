package com.tms.personnel.repository;

import com.tms.personnel.entity.PersonnelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<PersonnelEntity, Long> {

    Optional<PersonnelEntity> findByMatricule(String matricule);

    @Query("SELECT p FROM PersonnelEntity p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.matricule) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PersonnelEntity> search(@Param("query") String query, Pageable pageable);

    List<PersonnelEntity> findByTypeRessource(String typeRessource);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateEcheancePermis IS NOT NULL AND p.dateEcheancePermis <= :deadline")
    List<PersonnelEntity> findExpiringPermis(@Param("deadline") LocalDate deadline);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateProchaineVisiteMedicale IS NOT NULL AND p.dateProchaineVisiteMedicale <= :deadline")
    List<PersonnelEntity> findUpcomingVisiteMedicale(@Param("deadline") LocalDate deadline);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateProchainEntretien IS NOT NULL AND p.dateProchainEntretien <= :deadline")
    List<PersonnelEntity> findUpcomingEntretiens(@Param("deadline") LocalDate deadline);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateExpiration IS NOT NULL AND p.dateExpiration <= :deadline")
    List<PersonnelEntity> findExpiringCartesSejour(@Param("deadline") LocalDate deadline);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateValidite IS NOT NULL AND p.dateValidite < :today")
    List<PersonnelEntity> findExpiredFormations(@Param("today") LocalDate today);

    long countByTypeRessource(String typeRessource);

    long countByNatureContrat(String natureContrat);

    @Query("SELECT p FROM PersonnelEntity p WHERE p.dateFin IS NOT NULL AND p.dateFin >= :today")
    List<PersonnelEntity> findContratActifs(@Param("today") LocalDate today);

    Page<PersonnelEntity> findByTypeRessource(String typeRessource, Pageable pageable);

    Page<PersonnelEntity> findByNatureContrat(String natureContrat, Pageable pageable);
}
