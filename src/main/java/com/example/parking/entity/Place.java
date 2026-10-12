package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Column(nullable = false)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_place", nullable = false)
    private TypePlace type = TypePlace.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPlace statut = StatutPlace.LIBRE;
}
