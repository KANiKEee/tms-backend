package com.tms.camion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_camion_location",
       uniqueConstraints = @UniqueConstraint(columnNames = "camion_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamionLocationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_id")
    private Long missionId;

    @Column(name = "camion_id", nullable = false, unique = true)
    private Long camionId;

    @Column(name = "camion_immatricule")
    private String camionImmatricule;

    @Column(name = "chauffeur_id")
    private Long chauffeurId;

    @Column(name = "chauffeur_nom")
    private String chauffeurNom;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "speed")
    private Double speed; // km/h

    @Column(name = "heading")
    private Double heading; // degrees

    @Builder.Default
    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
}
