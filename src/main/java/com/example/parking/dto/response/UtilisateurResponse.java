package com.example.parking.dto.response;

import com.example.parking.entity.TypeUtilisateur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private TypeUtilisateur typeUtilisateur;
    private LocalDateTime dateCreation;
}
