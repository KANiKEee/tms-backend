package com.tms.personnel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonnelRequest {

    // Identité
    private String nom;
    private String prenom;
    private String matricule;
    private String civilite;
    private String nomNaissance;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String nationalite;
    private String etatCivile;
    private String situationFamiliale;
    private Long nombreEnfant;
    private String infosEnfants;
    private String numCarteSejour;
    private LocalDate dateExpiration;
    private String numeroSecuriteSociale;

    // Contact
    private String adresse;
    private String numeroAdresse;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String telFixe;
    private String telMobile;
    private String telephone;
    private String email;

    // Emploi
    private String typeRessource;
    private String reference;
    private String competence;
    private String prestataireTransport;
    private String natureContrat;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String horaireMensuel;
    private String posteDeTravail;
    private String indemniteKilometrique;
    private String niveauEtude;

    // Permis & Qualifications
    private String typePermisConduire;
    private String numeroPermis;
    private LocalDate dateEcheancePermis;
    private String numCarteConducteur;
    private LocalDate dateEcheanceCarteConducteur;
    private String numeroCarteQualificationConducteur;

    // Formation & Médical
    private String formations;
    private LocalDate dateFormations;
    private LocalDate dateValidite;
    private LocalDate dateProchaineVisiteMedicale;
    private String nombreJourAbsence;
    private Double heuresTravailParJour;
    private Integer soldeConge;

    // Entretiens
    private LocalDate dateDerniereEntretien;
    private LocalDate dateProchainEntretien;
    private LocalDate dateProchainEntretienDeuxAns;
    private LocalDate dateAutreEntretien;

    // Urgence
    private String personneAContacter;
    private String telephonePersonneAContacter;
    private String emailPersonneAContacter;

    // Compétences
    private String competencesString;
}
