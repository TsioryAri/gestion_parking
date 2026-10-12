package com.example.parking.service;

import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.response.UtilisateurResponse;

import java.util.List;

public interface UtilisateurService {
    UtilisateurResponse creerUtilisateur(UtilisateurRequest request);
    List<UtilisateurResponse> listerUtilisateurs();
    UtilisateurResponse obtenirUtilisateur(Long id);
    UtilisateurResponse modifierUtilisateur(Long id, UtilisateurRequest request);
    void supprimerUtilisateur(Long id);
}
