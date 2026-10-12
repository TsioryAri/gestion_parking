package com.example.parking.mapper;

import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.VehiculeResponse;
import com.example.parking.entity.Utilisateur;
import com.example.parking.entity.Vehicule;
import org.springframework.stereotype.Component;

@Component
public class VehiculeMapper {

    // "ab-123-cd " et "AB-123-CD" désignent le même véhicule
    public String normaliserImmatriculation(String immatriculation) {
        return immatriculation.trim().toUpperCase();
    }

    public void appliquer(Vehicule vehicule, VehiculeRequest request, Utilisateur proprietaire) {
        vehicule.setImmatriculation(normaliserImmatriculation(request.getImmatriculation()));
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        if (request.getTypeVehicule() != null) {
            vehicule.setTypeVehicule(request.getTypeVehicule());
        }
        vehicule.setUtilisateur(proprietaire);
    }

    public VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getTypeVehicule(),
                vehicule.getUtilisateur().getId(),
                vehicule.getUtilisateur().getNom()
        );
    }
}
