package com.example.parking.mapper;

import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.response.UtilisateurResponse;
import com.example.parking.entity.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    // Un email vide devient null : sinon deux utilisateurs sans email violeraient l'unicité
    public String normaliserEmail(String email) {
        return (email == null || email.isBlank()) ? null : email.trim().toLowerCase();
    }

    public Utilisateur toEntity(UtilisateurRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        appliquer(utilisateur, request);
        return utilisateur;
    }

    public void appliquer(Utilisateur utilisateur, UtilisateurRequest request) {
        utilisateur.setNom(request.getNom().trim());
        utilisateur.setEmail(normaliserEmail(request.getEmail()));
        utilisateur.setTelephone(request.getTelephone());
        if (request.getTypeUtilisateur() != null) {
            utilisateur.setTypeUtilisateur(request.getTypeUtilisateur());
        }
    }

    public UtilisateurResponse toResponse(Utilisateur utilisateur) {
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getTypeUtilisateur(),
                utilisateur.getDateCreation()
        );
    }
}
