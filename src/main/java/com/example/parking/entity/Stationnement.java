package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stationnements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stationnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    // Renseignée seulement si le véhicule est entré avec une réservation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "heure_entree", nullable = false)
    private LocalDateTime heureEntree;

    // Vide tant que le véhicule n'est pas sorti
    @Column(name = "heure_sortie")
    private LocalDateTime heureSortie;

    // Calculé à la sortie (Jour 9)
    @Column(precision = 10, scale = 2)
    private BigDecimal montant;
}
