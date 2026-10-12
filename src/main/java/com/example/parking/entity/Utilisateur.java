package com.example.parking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true)
    private String email;

    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_utilisateur", nullable = false)
    private TypeUtilisateur typeUtilisateur = TypeUtilisateur.STANDARD;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
