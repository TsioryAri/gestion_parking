package com.example.parking.dto.request;

import com.example.parking.entity.TypeVehicule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculeRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    private String marque;

    private String modele;

    // Optionnel : VOITURE par défaut
    private TypeVehicule typeVehicule;

    @NotNull(message = "Le propriétaire (utilisateurId) est obligatoire")
    private Long utilisateurId;
}
