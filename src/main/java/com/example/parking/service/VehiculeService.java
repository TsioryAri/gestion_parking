package com.example.parking.service;

import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.VehiculeResponse;

import java.util.List;

public interface VehiculeService {
    VehiculeResponse creerVehicule(VehiculeRequest request);
    List<VehiculeResponse> listerVehicules();
    VehiculeResponse obtenirVehicule(Long id);
    VehiculeResponse modifierVehicule(Long id, VehiculeRequest request);
    void supprimerVehicule(Long id);
}
