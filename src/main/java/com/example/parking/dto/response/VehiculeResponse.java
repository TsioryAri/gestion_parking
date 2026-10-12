package com.example.parking.dto.response;

import com.example.parking.entity.TypeVehicule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeResponse {
    private Long id;
    private String immatriculation;
    private String marque;
    private String modele;
    private TypeVehicule typeVehicule;
    private Long utilisateurId;
    private String utilisateurNom;
}
