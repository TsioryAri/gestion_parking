package com.example.parking.dto.request;

import com.example.parking.entity.TypeUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilisateurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Format d'email invalide")
    private String email;

    private String telephone;

    // Optionnel : STANDARD par défaut
    private TypeUtilisateur typeUtilisateur;
}
