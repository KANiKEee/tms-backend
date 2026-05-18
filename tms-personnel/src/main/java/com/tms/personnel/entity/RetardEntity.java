package com.tms.personnel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_retard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetardEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personnel_id", nullable = false)
    private Long personnelId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;

    @Column(name = "heure_prevue")
    private LocalTime heurePrevue;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(name = "motif")
    private String motif;

    @Column(name = "justifie")
    private boolean justifie;

    @Column(name = "commentaire", length = 1000)
    private String commentaire;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
