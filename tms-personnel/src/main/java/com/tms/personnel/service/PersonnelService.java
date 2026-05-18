package com.tms.personnel.service;

import com.tms.personnel.dto.ImportResultDTO;
import com.tms.personnel.dto.PersonnelRequest;
import com.tms.personnel.dto.PersonnelResponse;
import com.tms.personnel.dto.PersonnelStats;
import com.tms.personnel.entity.PersonnelEntity;
import com.tms.personnel.repository.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonnelService {

    private final PersonnelRepository repository;
    private final AbsenceRepository absenceRepository;
    private final CongeRepository congeRepository;
    private final RetardRepository retardRepository;
    private final MissionRepository missionRepository;

    // ═══ CRUD ═══

    public Page<PersonnelResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    public Page<PersonnelResponse> search(String query, Pageable pageable) {
        return repository.search(query, pageable).map(this::toResponse);
    }

    public Page<PersonnelResponse> findByTypeRessource(String typeRessource, Pageable pageable) {
        return repository.findByTypeRessource(typeRessource, pageable).map(this::toResponse);
    }

    public PersonnelResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'id: " + id));
    }

    @Transactional
    public PersonnelResponse create(PersonnelRequest request) {
        PersonnelEntity entity = toEntity(request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public PersonnelResponse update(Long id, PersonnelRequest request) {
        PersonnelEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'id: " + id));
        updateEntity(entity, request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Personnel non trouvé avec l'id: " + id);
        }
        // Cascade delete all related records
        absenceRepository.deleteByPersonnelId(id);
        congeRepository.deleteByPersonnelId(id);
        retardRepository.deleteByPersonnelId(id);
        missionRepository.deleteByChauffeurId(id);
        repository.deleteById(id);
    }

    // ═══ DASHBOARD KPIs ═══

    public PersonnelStats getStats() {
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);
        LocalDate in60Days = today.plusDays(60);
        LocalDate in7Days = today.plusDays(7);

        List<PersonnelEntity> all = repository.findAll();

        // Distribution par type de ressource
        Map<String, Long> parTypeRessource = all.stream()
                .filter(p -> p.getTypeRessource() != null)
                .collect(Collectors.groupingBy(PersonnelEntity::getTypeRessource, Collectors.counting()));

        // Distribution par nature de contrat
        Map<String, Long> parNatureContrat = all.stream()
                .filter(p -> p.getNatureContrat() != null)
                .collect(Collectors.groupingBy(PersonnelEntity::getNatureContrat, Collectors.counting()));

        // Distribution par ville
        Map<String, Long> parVille = all.stream()
                .filter(p -> p.getVille() != null)
                .collect(Collectors.groupingBy(PersonnelEntity::getVille, Collectors.counting()));

        // Distribution par type de permis
        Map<String, Long> parTypePermis = all.stream()
                .filter(p -> p.getTypePermisConduire() != null)
                .collect(Collectors.groupingBy(PersonnelEntity::getTypePermisConduire, Collectors.counting()));

        // Distribution par nationalité
        Map<String, Long> parNationalite = all.stream()
                .filter(p -> p.getNationalite() != null)
                .collect(Collectors.groupingBy(PersonnelEntity::getNationalite, Collectors.counting()));

        // Taux d'absence moyen
        double tauxAbsence = all.stream()
                .filter(p -> p.getNombreJourAbsence() != null)
                .mapToDouble(p -> {
                    try { return Double.parseDouble(p.getNombreJourAbsence()); }
                    catch (NumberFormatException e) { return 0; }
                })
                .average()
                .orElse(0);

        // Contrats actifs (dateFin null ou >= today)
        long contratActifs = all.stream()
                .filter(p -> p.getDateFin() == null || !p.getDateFin().isBefore(today))
                .count();

        long contratExpires = all.stream()
                .filter(p -> p.getDateFin() != null && p.getDateFin().isBefore(today))
                .count();

        return PersonnelStats.builder()
                .totalPersonnel(all.size())
                .totalChauffeurs(all.stream().filter(p -> "CHAUFFEUR".equalsIgnoreCase(p.getTypeRessource())).count())
                .contratActifs(contratActifs)
                .contratExpires(contratExpires)
                .permisExpirantSoon(repository.findExpiringPermis(in30Days).size())
                .visiteMedicaleSoon(repository.findUpcomingVisiteMedicale(in30Days).size())
                .entretiensSoon(repository.findUpcomingEntretiens(in7Days).size())
                .cartesSejourExpirantSoon(repository.findExpiringCartesSejour(in60Days).size())
                .formationsExpirees(repository.findExpiredFormations(today).size())
                .tauxAbsenceMoyen(Math.round(tauxAbsence * 100.0) / 100.0)
                .parTypeRessource(parTypeRessource)
                .parNatureContrat(parNatureContrat)
                .parVille(parVille)
                .parTypePermis(parTypePermis)
                .parNationalite(parNationalite)
                .build();
    }

    // ═══ MAPPING ═══

    private PersonnelResponse toResponse(PersonnelEntity e) {
        LocalDate today = LocalDate.now();
        return PersonnelResponse.builder()
                .id(e.getId())
                .nom(e.getNom())
                .prenom(e.getPrenom())
                .matricule(e.getMatricule())
                .civilite(e.getCivilite())
                .nomNaissance(e.getNomNaissance())
                .dateNaissance(e.getDateNaissance())
                .lieuNaissance(e.getLieuNaissance())
                .nationalite(e.getNationalite())
                .etatCivile(e.getEtatCivile())
                .situationFamiliale(e.getSituationFamiliale())
                .nombreEnfant(e.getNombreEnfant())
                .infosEnfants(e.getInfosEnfants())
                .numCarteSejour(e.getNumCarteSejour())
                .dateExpiration(e.getDateExpiration())
                .numeroSecuriteSociale(e.getNumeroSecuriteSociale())
                .adresse(e.getAdresse())
                .numeroAdresse(e.getNumeroAdresse())
                .rue(e.getRue())
                .ville(e.getVille())
                .codePostal(e.getCodePostal())
                .pays(e.getPays())
                .telFixe(e.getTelFixe())
                .telMobile(e.getTelMobile())
                .telephone(e.getTelephone())
                .email(e.getEmail())
                .typeRessource(e.getTypeRessource())
                .reference(e.getReference())
                .competence(e.getCompetence())
                .prestataireTransport(e.getPrestataireTransport())
                .natureContrat(e.getNatureContrat())
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .horaireMensuel(e.getHoraireMensuel())
                .posteDeTravail(e.getPosteDeTravail())
                .indemniteKilometrique(e.getIndemniteKilometrique())
                .niveauEtude(e.getNiveauEtude())
                .typePermisConduire(e.getTypePermisConduire())
                .numeroPermis(e.getNumeroPermis())
                .dateEcheancePermis(e.getDateEcheancePermis())
                .numCarteConducteur(e.getNumCarteConducteur())
                .dateEcheanceCarteConducteur(e.getDateEcheanceCarteConducteur())
                .numeroCarteQualificationConducteur(e.getNumeroCarteQualificationConducteur())
                .formations(e.getFormations())
                .dateFormations(e.getDateFormations())
                .dateValidite(e.getDateValidite())
                .dateProchaineVisiteMedicale(e.getDateProchaineVisiteMedicale())
                .nombreJourAbsence(e.getNombreJourAbsence())
                .dateDerniereEntretien(e.getDateDerniereEntretien())
                .dateProchainEntretien(e.getDateProchainEntretien())
                .dateProchainEntretienDeuxAns(e.getDateProchainEntretienDeuxAns())
                .dateAutreEntretien(e.getDateAutreEntretien())
                .personneAContacter(e.getPersonneAContacter())
                .telephonePersonneAContacter(e.getTelephonePersonneAContacter())
                .emailPersonneAContacter(e.getEmailPersonneAContacter())
                .competencesString(e.getCompetencesString())
                .heuresTravailParJour(e.getHeuresTravailParJour())
                .soldeConge(e.getSoldeConge())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                // Computed flags
                .permisExpireSoon(e.getDateEcheancePermis() != null && !e.getDateEcheancePermis().isAfter(today.plusDays(30)))
                .visiteMedicaleSoon(e.getDateProchaineVisiteMedicale() != null && !e.getDateProchaineVisiteMedicale().isAfter(today.plusDays(30)))
                .contratActif(e.getDateFin() == null || !e.getDateFin().isBefore(today))
                .build();
    }

    private PersonnelEntity toEntity(PersonnelRequest r) {
        return PersonnelEntity.builder()
                .nom(r.getNom())
                .prenom(r.getPrenom())
                .matricule(r.getMatricule())
                .civilite(r.getCivilite())
                .nomNaissance(r.getNomNaissance())
                .dateNaissance(r.getDateNaissance())
                .lieuNaissance(r.getLieuNaissance())
                .nationalite(r.getNationalite())
                .etatCivile(r.getEtatCivile())
                .situationFamiliale(r.getSituationFamiliale())
                .nombreEnfant(r.getNombreEnfant())
                .infosEnfants(r.getInfosEnfants())
                .numCarteSejour(r.getNumCarteSejour())
                .dateExpiration(r.getDateExpiration())
                .numeroSecuriteSociale(r.getNumeroSecuriteSociale())
                .adresse(r.getAdresse())
                .numeroAdresse(r.getNumeroAdresse())
                .rue(r.getRue())
                .ville(r.getVille())
                .codePostal(r.getCodePostal())
                .pays(r.getPays())
                .telFixe(r.getTelFixe())
                .telMobile(r.getTelMobile())
                .telephone(r.getTelephone())
                .email(r.getEmail())
                .typeRessource(r.getTypeRessource())
                .reference(r.getReference())
                .competence(r.getCompetence())
                .prestataireTransport(r.getPrestataireTransport())
                .natureContrat(r.getNatureContrat())
                .dateDebut(r.getDateDebut())
                .dateFin(r.getDateFin())
                .horaireMensuel(r.getHoraireMensuel())
                .posteDeTravail(r.getPosteDeTravail())
                .indemniteKilometrique(r.getIndemniteKilometrique())
                .niveauEtude(r.getNiveauEtude())
                .typePermisConduire(r.getTypePermisConduire())
                .numeroPermis(r.getNumeroPermis())
                .dateEcheancePermis(r.getDateEcheancePermis())
                .numCarteConducteur(r.getNumCarteConducteur())
                .dateEcheanceCarteConducteur(r.getDateEcheanceCarteConducteur())
                .numeroCarteQualificationConducteur(r.getNumeroCarteQualificationConducteur())
                .formations(r.getFormations())
                .dateFormations(r.getDateFormations())
                .dateValidite(r.getDateValidite())
                .dateProchaineVisiteMedicale(r.getDateProchaineVisiteMedicale())
                .nombreJourAbsence(r.getNombreJourAbsence())
                .dateDerniereEntretien(r.getDateDerniereEntretien())
                .dateProchainEntretien(r.getDateProchainEntretien())
                .dateProchainEntretienDeuxAns(r.getDateProchainEntretienDeuxAns())
                .dateAutreEntretien(r.getDateAutreEntretien())
                .personneAContacter(r.getPersonneAContacter())
                .telephonePersonneAContacter(r.getTelephonePersonneAContacter())
                .emailPersonneAContacter(r.getEmailPersonneAContacter())
                .competencesString(r.getCompetencesString())
                .heuresTravailParJour(r.getHeuresTravailParJour() != null ? r.getHeuresTravailParJour() : 8.0)
                .soldeConge(r.getSoldeConge() != null ? r.getSoldeConge() : 25)
                .build();
    }

    private void updateEntity(PersonnelEntity e, PersonnelRequest r) {
        if (r.getNom() != null) e.setNom(r.getNom());
        if (r.getPrenom() != null) e.setPrenom(r.getPrenom());
        if (r.getMatricule() != null) e.setMatricule(r.getMatricule());
        if (r.getCivilite() != null) e.setCivilite(r.getCivilite());
        if (r.getNomNaissance() != null) e.setNomNaissance(r.getNomNaissance());
        if (r.getDateNaissance() != null) e.setDateNaissance(r.getDateNaissance());
        if (r.getLieuNaissance() != null) e.setLieuNaissance(r.getLieuNaissance());
        if (r.getNationalite() != null) e.setNationalite(r.getNationalite());
        if (r.getEtatCivile() != null) e.setEtatCivile(r.getEtatCivile());
        if (r.getSituationFamiliale() != null) e.setSituationFamiliale(r.getSituationFamiliale());
        if (r.getNombreEnfant() != null) e.setNombreEnfant(r.getNombreEnfant());
        if (r.getInfosEnfants() != null) e.setInfosEnfants(r.getInfosEnfants());
        if (r.getNumCarteSejour() != null) e.setNumCarteSejour(r.getNumCarteSejour());
        if (r.getDateExpiration() != null) e.setDateExpiration(r.getDateExpiration());
        if (r.getNumeroSecuriteSociale() != null) e.setNumeroSecuriteSociale(r.getNumeroSecuriteSociale());
        if (r.getAdresse() != null) e.setAdresse(r.getAdresse());
        if (r.getNumeroAdresse() != null) e.setNumeroAdresse(r.getNumeroAdresse());
        if (r.getRue() != null) e.setRue(r.getRue());
        if (r.getVille() != null) e.setVille(r.getVille());
        if (r.getCodePostal() != null) e.setCodePostal(r.getCodePostal());
        if (r.getPays() != null) e.setPays(r.getPays());
        if (r.getTelFixe() != null) e.setTelFixe(r.getTelFixe());
        if (r.getTelMobile() != null) e.setTelMobile(r.getTelMobile());
        if (r.getTelephone() != null) e.setTelephone(r.getTelephone());
        if (r.getEmail() != null) e.setEmail(r.getEmail());
        if (r.getTypeRessource() != null) e.setTypeRessource(r.getTypeRessource());
        if (r.getReference() != null) e.setReference(r.getReference());
        if (r.getCompetence() != null) e.setCompetence(r.getCompetence());
        if (r.getPrestataireTransport() != null) e.setPrestataireTransport(r.getPrestataireTransport());
        if (r.getNatureContrat() != null) e.setNatureContrat(r.getNatureContrat());
        if (r.getDateDebut() != null) e.setDateDebut(r.getDateDebut());
        if (r.getDateFin() != null) e.setDateFin(r.getDateFin());
        if (r.getHoraireMensuel() != null) e.setHoraireMensuel(r.getHoraireMensuel());
        if (r.getPosteDeTravail() != null) e.setPosteDeTravail(r.getPosteDeTravail());
        if (r.getIndemniteKilometrique() != null) e.setIndemniteKilometrique(r.getIndemniteKilometrique());
        if (r.getNiveauEtude() != null) e.setNiveauEtude(r.getNiveauEtude());
        if (r.getTypePermisConduire() != null) e.setTypePermisConduire(r.getTypePermisConduire());
        if (r.getNumeroPermis() != null) e.setNumeroPermis(r.getNumeroPermis());
        if (r.getDateEcheancePermis() != null) e.setDateEcheancePermis(r.getDateEcheancePermis());
        if (r.getNumCarteConducteur() != null) e.setNumCarteConducteur(r.getNumCarteConducteur());
        if (r.getDateEcheanceCarteConducteur() != null) e.setDateEcheanceCarteConducteur(r.getDateEcheanceCarteConducteur());
        if (r.getNumeroCarteQualificationConducteur() != null) e.setNumeroCarteQualificationConducteur(r.getNumeroCarteQualificationConducteur());
        if (r.getFormations() != null) e.setFormations(r.getFormations());
        if (r.getDateFormations() != null) e.setDateFormations(r.getDateFormations());
        if (r.getDateValidite() != null) e.setDateValidite(r.getDateValidite());
        if (r.getDateProchaineVisiteMedicale() != null) e.setDateProchaineVisiteMedicale(r.getDateProchaineVisiteMedicale());
        if (r.getNombreJourAbsence() != null) e.setNombreJourAbsence(r.getNombreJourAbsence());
        if (r.getDateDerniereEntretien() != null) e.setDateDerniereEntretien(r.getDateDerniereEntretien());
        if (r.getDateProchainEntretien() != null) e.setDateProchainEntretien(r.getDateProchainEntretien());
        if (r.getDateProchainEntretienDeuxAns() != null) e.setDateProchainEntretienDeuxAns(r.getDateProchainEntretienDeuxAns());
        if (r.getDateAutreEntretien() != null) e.setDateAutreEntretien(r.getDateAutreEntretien());
        if (r.getPersonneAContacter() != null) e.setPersonneAContacter(r.getPersonneAContacter());
        if (r.getTelephonePersonneAContacter() != null) e.setTelephonePersonneAContacter(r.getTelephonePersonneAContacter());
        if (r.getEmailPersonneAContacter() != null) e.setEmailPersonneAContacter(r.getEmailPersonneAContacter());
        if (r.getCompetencesString() != null) e.setCompetencesString(r.getCompetencesString());
        if (r.getHeuresTravailParJour() != null) e.setHeuresTravailParJour(r.getHeuresTravailParJour());
        if (r.getSoldeConge() != null) e.setSoldeConge(r.getSoldeConge());
    }

    // ═══ IMPORT CSV / EXCEL ═══

    private static final Map<String, String> COLUMN_MAP = new LinkedHashMap<>();
    static {
        COLUMN_MAP.put("nom", "nom");
        COLUMN_MAP.put("prenom", "prenom");
        COLUMN_MAP.put("prénom", "prenom");
        COLUMN_MAP.put("matricule", "matricule");
        COLUMN_MAP.put("civilite", "civilite");
        COLUMN_MAP.put("civilité", "civilite");
        COLUMN_MAP.put("nom naissance", "nomNaissance");
        COLUMN_MAP.put("nom de naissance", "nomNaissance");
        COLUMN_MAP.put("date naissance", "dateNaissance");
        COLUMN_MAP.put("date de naissance", "dateNaissance");
        COLUMN_MAP.put("lieu naissance", "lieuNaissance");
        COLUMN_MAP.put("lieu de naissance", "lieuNaissance");
        COLUMN_MAP.put("nationalite", "nationalite");
        COLUMN_MAP.put("nationalité", "nationalite");
        COLUMN_MAP.put("etat civil", "etatCivile");
        COLUMN_MAP.put("état civil", "etatCivile");
        COLUMN_MAP.put("etat civile", "etatCivile");
        COLUMN_MAP.put("situation familiale", "situationFamiliale");
        COLUMN_MAP.put("nombre enfant", "nombreEnfant");
        COLUMN_MAP.put("nbr enfant", "nombreEnfant");
        COLUMN_MAP.put("infos enfants", "infosEnfants");
        COLUMN_MAP.put("num carte sejour", "numCarteSejour");
        COLUMN_MAP.put("numéro carte séjour", "numCarteSejour");
        COLUMN_MAP.put("date expiration", "dateExpiration");
        COLUMN_MAP.put("numero securite sociale", "numeroSecuriteSociale");
        COLUMN_MAP.put("numéro sécurité sociale", "numeroSecuriteSociale");
        COLUMN_MAP.put("adresse", "adresse");
        COLUMN_MAP.put("numero adresse", "numeroAdresse");
        COLUMN_MAP.put("numéro adresse", "numeroAdresse");
        COLUMN_MAP.put("rue", "rue");
        COLUMN_MAP.put("ville", "ville");
        COLUMN_MAP.put("code postal", "codePostal");
        COLUMN_MAP.put("pays", "pays");
        COLUMN_MAP.put("tel fixe", "telFixe");
        COLUMN_MAP.put("téléphone fixe", "telFixe");
        COLUMN_MAP.put("tel mobile", "telMobile");
        COLUMN_MAP.put("téléphone mobile", "telMobile");
        COLUMN_MAP.put("telephone", "telephone");
        COLUMN_MAP.put("téléphone", "telephone");
        COLUMN_MAP.put("email", "email");
        COLUMN_MAP.put("type ressource", "typeRessource");
        COLUMN_MAP.put("type de ressource", "typeRessource");
        COLUMN_MAP.put("typeressource", "typeRessource");
        COLUMN_MAP.put("reference", "reference");
        COLUMN_MAP.put("référence", "reference");
        COLUMN_MAP.put("competence", "competence");
        COLUMN_MAP.put("compétence", "competence");
        COLUMN_MAP.put("prestataire transport", "prestataireTransport");
        COLUMN_MAP.put("nature contrat", "natureContrat");
        COLUMN_MAP.put("nature du contrat", "natureContrat");
        COLUMN_MAP.put("date debut", "dateDebut");
        COLUMN_MAP.put("date de début", "dateDebut");
        COLUMN_MAP.put("date début", "dateDebut");
        COLUMN_MAP.put("date fin", "dateFin");
        COLUMN_MAP.put("date de fin", "dateFin");
        COLUMN_MAP.put("horaire mensuel", "horaireMensuel");
        COLUMN_MAP.put("poste de travail", "posteDeTravail");
        COLUMN_MAP.put("poste travail", "posteDeTravail");
        COLUMN_MAP.put("indemnite kilometrique", "indemniteKilometrique");
        COLUMN_MAP.put("indemnité kilométrique", "indemniteKilometrique");
        COLUMN_MAP.put("niveau etude", "niveauEtude");
        COLUMN_MAP.put("niveau d'étude", "niveauEtude");
        COLUMN_MAP.put("niveau étude", "niveauEtude");
        COLUMN_MAP.put("type permis conduire", "typePermisConduire");
        COLUMN_MAP.put("type permis", "typePermisConduire");
        COLUMN_MAP.put("type de permis", "typePermisConduire");
        COLUMN_MAP.put("numero permis", "numeroPermis");
        COLUMN_MAP.put("numéro permis", "numeroPermis");
        COLUMN_MAP.put("date echeance permis", "dateEcheancePermis");
        COLUMN_MAP.put("date échéance permis", "dateEcheancePermis");
        COLUMN_MAP.put("num carte conducteur", "numCarteConducteur");
        COLUMN_MAP.put("numéro carte conducteur", "numCarteConducteur");
        COLUMN_MAP.put("date echeance carte conducteur", "dateEcheanceCarteConducteur");
        COLUMN_MAP.put("date échéance carte conducteur", "dateEcheanceCarteConducteur");
        COLUMN_MAP.put("numero carte qualification conducteur", "numeroCarteQualificationConducteur");
        COLUMN_MAP.put("formations", "formations");
        COLUMN_MAP.put("date formations", "dateFormations");
        COLUMN_MAP.put("date validite", "dateValidite");
        COLUMN_MAP.put("date validité", "dateValidite");
        COLUMN_MAP.put("date prochaine visite medicale", "dateProchaineVisiteMedicale");
        COLUMN_MAP.put("date prochaine visite médicale", "dateProchaineVisiteMedicale");
        COLUMN_MAP.put("nbr jours absence", "nombreJourAbsence");
        COLUMN_MAP.put("nombre jours absence", "nombreJourAbsence");
        COLUMN_MAP.put("date derniere entretien", "dateDerniereEntretien");
        COLUMN_MAP.put("date dernière entretien", "dateDerniereEntretien");
        COLUMN_MAP.put("date prochain entretien", "dateProchainEntretien");
        COLUMN_MAP.put("date prochain entretien deux ans", "dateProchainEntretienDeuxAns");
        COLUMN_MAP.put("date autre entretien", "dateAutreEntretien");
        COLUMN_MAP.put("personne a contacter", "personneAContacter");
        COLUMN_MAP.put("personne à contacter", "personneAContacter");
        COLUMN_MAP.put("telephone personne a contacter", "telephonePersonneAContacter");
        COLUMN_MAP.put("téléphone personne à contacter", "telephonePersonneAContacter");
        COLUMN_MAP.put("email personne a contacter", "emailPersonneAContacter");
        COLUMN_MAP.put("email personne à contacter", "emailPersonneAContacter");
        COLUMN_MAP.put("competences", "competencesString");
        COLUMN_MAP.put("compétences", "competencesString");
    }

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    );

    private static final Set<String> DATE_FIELDS = Set.of(
            "dateNaissance", "dateExpiration", "dateDebut", "dateFin",
            "dateEcheancePermis", "dateEcheanceCarteConducteur",
            "dateFormations", "dateValidite", "dateProchaineVisiteMedicale",
            "dateDerniereEntretien", "dateProchainEntretien",
            "dateProchainEntretienDeuxAns", "dateAutreEntretien"
    );

    @Transactional
    public ImportResultDTO importFromFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new RuntimeException("Nom de fichier manquant");
        }

        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        List<String[]> rows;
        try {
            if ("csv".equals(ext)) {
                rows = parseCsv(file);
            } else if ("xlsx".equals(ext) || "xls".equals(ext)) {
                rows = parseExcel(file);
            } else {
                throw new RuntimeException("Format non supporté. Utilisez .csv, .xlsx ou .xls");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier: " + e.getMessage());
        }

        if (rows.size() < 2) {
            throw new RuntimeException("Le fichier doit contenir au moins une ligne d'en-tête et une ligne de données");
        }

        // Map header columns
        String[] headers = rows.get(0);
        String[] mappedFields = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            String normalized = headers[i].trim().toLowerCase()
                    .replaceAll("[_\\-]", " ")
                    .replaceAll("\\s+", " ");
            mappedFields[i] = COLUMN_MAP.getOrDefault(normalized, null);
        }

        List<ImportResultDTO.ImportError> errors = new ArrayList<>();
        List<PersonnelEntity> toSave = new ArrayList<>();
        int totalDataRows = rows.size() - 1;

        for (int rowIdx = 1; rowIdx < rows.size(); rowIdx++) {
            String[] values = rows.get(rowIdx);
            // Skip completely empty rows
            if (Arrays.stream(values).allMatch(v -> v == null || v.trim().isEmpty())) {
                totalDataRows--;
                continue;
            }

            try {
                PersonnelEntity entity = buildEntityFromRow(mappedFields, values, rowIdx + 1, errors);
                if (entity != null) {
                    toSave.add(entity);
                }
            } catch (Exception e) {
                errors.add(ImportResultDTO.ImportError.builder()
                        .row(rowIdx + 1)
                        .field("*")
                        .message("Erreur inattendue: " + e.getMessage())
                        .build());
            }
        }

        // Save valid entities
        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }

        return ImportResultDTO.builder()
                .totalRows(totalDataRows)
                .successCount(toSave.size())
                .errorCount(totalDataRows - toSave.size())
                .errors(errors)
                .build();
    }

    private List<String[]> parseCsv(MultipartFile file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        }
        return rows;
    }

    private List<String[]> parseExcel(MultipartFile file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                int lastCell = row.getLastCellNum();
                String[] values = new String[lastCell];
                for (int i = 0; i < lastCell; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    values[i] = cell != null ? formatter.formatCellValue(cell).trim() : "";
                }
                rows.add(values);
            }
        }
        return rows;
    }

    private PersonnelEntity buildEntityFromRow(String[] mappedFields, String[] values,
                                                int rowNum, List<ImportResultDTO.ImportError> errors) {
        PersonnelEntity entity = new PersonnelEntity();
        boolean hasError = false;

        for (int i = 0; i < mappedFields.length && i < values.length; i++) {
            String field = mappedFields[i];
            if (field == null) continue;
            String value = values[i] != null ? values[i].trim() : "";
            if (value.isEmpty()) continue;

            try {
                if (DATE_FIELDS.contains(field)) {
                    LocalDate date = parseDate(value);
                    if (date == null) {
                        errors.add(ImportResultDTO.ImportError.builder()
                                .row(rowNum).field(field)
                                .message("Format de date invalide: " + value)
                                .build());
                        hasError = true;
                        continue;
                    }
                    setDateField(entity, field, date);
                } else if ("nombreEnfant".equals(field)) {
                    try {
                        entity.setNombreEnfant(Long.parseLong(value));
                    } catch (NumberFormatException e) {
                        errors.add(ImportResultDTO.ImportError.builder()
                                .row(rowNum).field(field)
                                .message("Nombre invalide: " + value)
                                .build());
                        hasError = true;
                    }
                } else {
                    setStringField(entity, field, value);
                }
            } catch (Exception e) {
                errors.add(ImportResultDTO.ImportError.builder()
                        .row(rowNum).field(field)
                        .message("Erreur: " + e.getMessage())
                        .build());
                hasError = true;
            }
        }

        // Must have at least nom or prenom
        if ((entity.getNom() == null || entity.getNom().isEmpty())
                && (entity.getPrenom() == null || entity.getPrenom().isEmpty())) {
            errors.add(ImportResultDTO.ImportError.builder()
                    .row(rowNum).field("nom/prenom")
                    .message("Le nom ou le prénom est requis")
                    .build());
            return null;
        }

        return hasError ? null : entity;
    }

    private LocalDate parseDate(String value) {
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, fmt);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private void setDateField(PersonnelEntity e, String field, LocalDate date) {
        switch (field) {
            case "dateNaissance" -> e.setDateNaissance(date);
            case "dateExpiration" -> e.setDateExpiration(date);
            case "dateDebut" -> e.setDateDebut(date);
            case "dateFin" -> e.setDateFin(date);
            case "dateEcheancePermis" -> e.setDateEcheancePermis(date);
            case "dateEcheanceCarteConducteur" -> e.setDateEcheanceCarteConducteur(date);
            case "dateFormations" -> e.setDateFormations(date);
            case "dateValidite" -> e.setDateValidite(date);
            case "dateProchaineVisiteMedicale" -> e.setDateProchaineVisiteMedicale(date);
            case "dateDerniereEntretien" -> e.setDateDerniereEntretien(date);
            case "dateProchainEntretien" -> e.setDateProchainEntretien(date);
            case "dateProchainEntretienDeuxAns" -> e.setDateProchainEntretienDeuxAns(date);
            case "dateAutreEntretien" -> e.setDateAutreEntretien(date);
        }
    }

    private void setStringField(PersonnelEntity e, String field, String value) {
        switch (field) {
            case "nom" -> e.setNom(value);
            case "prenom" -> e.setPrenom(value);
            case "matricule" -> e.setMatricule(value);
            case "civilite" -> e.setCivilite(value);
            case "nomNaissance" -> e.setNomNaissance(value);
            case "lieuNaissance" -> e.setLieuNaissance(value);
            case "nationalite" -> e.setNationalite(value);
            case "etatCivile" -> e.setEtatCivile(value);
            case "situationFamiliale" -> e.setSituationFamiliale(value);
            case "infosEnfants" -> e.setInfosEnfants(value);
            case "numCarteSejour" -> e.setNumCarteSejour(value);
            case "numeroSecuriteSociale" -> e.setNumeroSecuriteSociale(value);
            case "adresse" -> e.setAdresse(value);
            case "numeroAdresse" -> e.setNumeroAdresse(value);
            case "rue" -> e.setRue(value);
            case "ville" -> e.setVille(value);
            case "codePostal" -> e.setCodePostal(value);
            case "pays" -> e.setPays(value);
            case "telFixe" -> e.setTelFixe(value);
            case "telMobile" -> e.setTelMobile(value);
            case "telephone" -> e.setTelephone(value);
            case "email" -> e.setEmail(value);
            case "typeRessource" -> e.setTypeRessource(value);
            case "reference" -> e.setReference(value);
            case "competence" -> e.setCompetence(value);
            case "prestataireTransport" -> e.setPrestataireTransport(value);
            case "natureContrat" -> e.setNatureContrat(value);
            case "horaireMensuel" -> e.setHoraireMensuel(value);
            case "posteDeTravail" -> e.setPosteDeTravail(value);
            case "indemniteKilometrique" -> e.setIndemniteKilometrique(value);
            case "niveauEtude" -> e.setNiveauEtude(value);
            case "typePermisConduire" -> e.setTypePermisConduire(value);
            case "numeroPermis" -> e.setNumeroPermis(value);
            case "numCarteConducteur" -> e.setNumCarteConducteur(value);
            case "numeroCarteQualificationConducteur" -> e.setNumeroCarteQualificationConducteur(value);
            case "formations" -> e.setFormations(value);
            case "nombreJourAbsence" -> e.setNombreJourAbsence(value);
            case "personneAContacter" -> e.setPersonneAContacter(value);
            case "telephonePersonneAContacter" -> e.setTelephonePersonneAContacter(value);
            case "emailPersonneAContacter" -> e.setEmailPersonneAContacter(value);
            case "competencesString" -> e.setCompetencesString(value);
        }
    }
}
