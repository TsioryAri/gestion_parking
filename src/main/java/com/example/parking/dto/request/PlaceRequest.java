package com.example.parking.dto.request;

import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.TypePlace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceRequest {

    @NotBlank(message = "Le numéro de la place est obligatoire")
    private String numero;

    @NotBlank(message = "La zone est obligatoire")
    private String zone;

    @NotNull(message = "Le type de place est obligatoire")
    private TypePlace type;

    // Optionnel : LIBRE par défaut
    private StatutPlace statut;
}
