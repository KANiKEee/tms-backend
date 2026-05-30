package com.tms.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_stock", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"produit_id", "zone_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @Column(name = "quantite", nullable = false)
    @Builder.Default
    private Integer quantite = 0;

    @Builder.Default
    @Column(name = "derniere_maj")
    private LocalDateTime derniereMaj = LocalDateTime.now();
}
