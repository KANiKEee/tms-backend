package com.tms.personnel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gl_document")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fichier")
    private String nomFichier;

    @Column(name = "type_fichier")
    private String typeFichier;

    @Column(name = "type_document")
    private String typeDocument;

    @Lob
    @Column(name = "contenu")
    private byte[] contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PersonnelEntity personnelEntity;
}
