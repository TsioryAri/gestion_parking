package com.example.parking.service.impl;

import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.VehiculeResponse;
import com.example.parking.entity.Utilisateur;
import com.example.parking.entity.Vehicule;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.VehiculeMapper;
import com.example.parking.repository.UtilisateurRepository;
import com.example.parking.repository.VehiculeRepository;
import com.example.parking.service.VehiculeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeServiceImpl(VehiculeRepository vehiculeRepository,
                               UtilisateurRepository utilisateurRepository,
                               VehiculeMapper vehiculeMapper) {
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    @Transactional
    public VehiculeResponse creerVehicule(VehiculeRequest request) {
        Utilisateur proprietaire = trouverProprietaire(request.getUtilisateurId());
        String immatriculation = vehiculeMapper.normaliserImmatriculation(request.getImmatriculation());
        if (vehiculeRepository.existsByImmatriculation(immatriculation)) {
            throw new ConflictException("Un véhicule avec l'immatriculation " + immatriculation + " existe déjà");
        }
        Vehicule vehicule = new Vehicule();
        vehiculeMapper.appliquer(vehicule, request, proprietaire);
        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> listerVehicules() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse obtenirVehicule(Long id) {
        return vehiculeMapper.toResponse(trouver(id));
    }

    @Override
    @Transactional
    public VehiculeResponse modifierVehicule(Long id, VehiculeRequest request) {
        Vehicule vehicule = trouver(id);
        Utilisateur proprietaire = trouverProprietaire(request.getUtilisateurId());
        String immatriculation = vehiculeMapper.normaliserImmatriculation(request.getImmatriculation());
        if (!immatriculation.equals(vehicule.getImmatriculation())
                && vehiculeRepository.existsByImmatriculation(immatriculation)) {
            throw new ConflictException("Un véhicule avec l'immatriculation " + immatriculation + " existe déjà");
        }
        vehiculeMapper.appliquer(vehicule, request, proprietaire);
        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional
    public void supprimerVehicule(Long id) {
        Vehicule vehicule = trouver(id);
        vehiculeRepository.delete(vehicule);
        // flush : si une réservation, un abonnement ou un stationnement référence ce véhicule,
        // l'erreur remonte ici et devient une réponse 409 propre
        vehiculeRepository.flush();
    }

    private Vehicule trouver(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'id " + id));
    }

    private Utilisateur trouverProprietaire(Long utilisateurId) {
        return utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id " + utilisateurId));
    }
}
