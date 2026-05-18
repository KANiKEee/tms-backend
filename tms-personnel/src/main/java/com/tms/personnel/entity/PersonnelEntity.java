package com.tms.personnel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "gl_personnel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelEntity implements Serializable {

    private static final long serialVersionUID = 669811434041093587L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ═══ IDENTITÉ ═══
    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "matricule", unique = true)
    private String matricule;

    @Column(name = "civilite")
    private String civilite;

    @Column(name = "nom_naissance")
    private String nomNaissance;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance")
    private String lieuNaissance;

    @Column(name = "nationalite")
    private String nationalite;

    @Column(name = "etat_civile")
    private String etatCivile;

    @Column(name = "situation_familiale")
    private String situationFamiliale;

    @Column(name = "nbr_enfant")
    private Long nombreEnfant;

    @Column(name = "infos_enfants")
    private String infosEnfants;

    @Column(name = "num_carte_sejour")
    private String numCarteSejour;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Column(name = "numero_securite_sociale")
    private String numeroSecuriteSociale;

    // ═══ CONTACT ═══
    @Column(name = "adresse")
    private String adresse;

    @Column(name = "numero_adresse")
    private String numeroAdresse;

    @Column(name = "rue")
    private String rue;

    @Column(name = "ville")
    private String ville;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "pays")
    private String pays;

    @Column(name = "tel_fixe")
    private String telFixe;

    @Column(name = "tel_mobile")
    private String telMobile;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    // ═══ EMPLOI ═══
    @Column(name = "type_ressource")
    private String typeRessource;

    @Column(name = "reference")
    private String reference;

    @Column(name = "competence")
    private String competence;

    @Column(name = "prestataire_transport")
    private String prestataireTransport;

    @Column(name = "nature_contrat")
    private String natureContrat;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "horaire_mensuel")
    private String horaireMensuel;

    @Column(name = "poste_travail")
    private String posteDeTravail;

    @Column(name = "indemnite_kilometrique")
    private String indemniteKilometrique;

    @Column(name = "niveau_etude")
    private String niveauEtude;

    // ═══ PERMIS & QUALIFICATIONS ═══
    @Column(name = "type_permis_conduire")
    private String typePermisConduire;

    @Column(name = "numero_permis")
    private String numeroPermis;

    @Column(name = "date_echeance_permis")
    private LocalDate dateEcheancePermis;

    @Column(name = "num_carte_conducteur")
    private String numCarteConducteur;

    @Column(name = "date_echeance_carte_conducteur")
    private LocalDate dateEcheanceCarteConducteur;

    @Column(name = "numero_carte_qualification_conducteur")
    private String numeroCarteQualificationConducteur;

    // ═══ FORMATION & MÉDICAL ═══
    @Column(name = "formations")
    private String formations;

    @Column(name = "date_formations")
    private LocalDate dateFormations;

    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @Column(name = "date_prochaine_visite_medicale")
    private LocalDate dateProchaineVisiteMedicale;

    @Column(name = "nbr_jours_absence")
    private String nombreJourAbsence;

    @Column(name = "heures_travail_jour")
    private Double heuresTravailParJour;

    @Column(name = "solde_conge")
    private Integer soldeConge;

    // ═══ ENTRETIENS ═══
    @Column(name = "date_derniere_entretien")
    private LocalDate dateDerniereEntretien;

    @Column(name = "date_prochain_entretien")
    private LocalDate dateProchainEntretien;

    @Column(name = "date_prochain_entretien_deux_ans")
    private LocalDate dateProchainEntretienDeuxAns;

    @Column(name = "date_autre_entretien")
    private LocalDate dateAutreEntretien;

    // ═══ URGENCE ═══
    @Column(name = "personne_a_contacter")
    private String personneAContacter;

    @Column(name = "telephone_personne_a_contacter")
    private String telephonePersonneAContacter;

    @Column(name = "email_personne_a_contacter")
    private String emailPersonneAContacter;

    // ═══ COMPÉTENCES & DOCUMENTS ═══
    @Column(name = "competences_string")
    private String competencesString;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "personnelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompetenceEntite> competenceEntites = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "personnelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentEntite> documentEntites = new HashSet<>();

    // ═══ AUDIT ═══
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
