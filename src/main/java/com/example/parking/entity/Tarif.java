package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tarifs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"zone", "type_vehicule"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_vehicule", nullable = false)
    private TypeVehicule typeVehicule;

    @Column(name = "prix_horaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixHoraire;

    @Column(name = "tarif_minimum", precision = 10, scale = 2)
    private BigDecimal tarifMinimum;

    @Column(name = "forfait_journalier", precision = 10, scale = 2)
    private BigDecimal forfaitJournalier;

    @Column(name = "majoration_depassement", precision = 5, scale = 2)
    private BigDecimal majorationDepassement;

    @Column(name = "reduction_abonne", precision = 5, scale = 2)
    private BigDecimal reductionAbonne;
}
