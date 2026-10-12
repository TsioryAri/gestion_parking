package com.example.parking.service.impl;

import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.response.UtilisateurResponse;
import com.example.parking.entity.Utilisateur;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.UtilisateurMapper;
import com.example.parking.repository.UtilisateurRepository;
import com.example.parking.repository.VehiculeRepository;
import com.example.parking.service.UtilisateurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  VehiculeRepository vehiculeRepository,
                                  UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    @Override
    @Transactional
    public UtilisateurResponse creerUtilisateur(UtilisateurRequest request) {
        String email = utilisateurMapper.normaliserEmail(request.getEmail());
        if (email != null && utilisateurRepository.existsByEmail(email)) {
            throw new ConflictException("Un utilisateur avec l'email " + email + " existe déjà");
        }
        Utilisateur utilisateur = utilisateurRepository.save(utilisateurMapper.toEntity(request));
        return utilisateurMapper.toResponse(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse obtenirUtilisateur(Long id) {
        return utilisateurMapper.toResponse(trouver(id));
    }

    @Override
    @Transactional
    public UtilisateurResponse modifierUtilisateur(Long id, UtilisateurRequest request) {
        Utilisateur utilisateur = trouver(id);
        String email = utilisateurMapper.normaliserEmail(request.getEmail());
        if (email != null && !email.equals(utilisateur.getEmail()) && utilisateurRepository.existsByEmail(email)) {
            throw new ConflictException("Un utilisateur avec l'email " + email + " existe déjà");
        }
        utilisateurMapper.appliquer(utilisateur, request);
        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional
    public void supprimerUtilisateur(Long id) {
        Utilisateur utilisateur = trouver(id);
        if (vehiculeRepository.existsByUtilisateurId(id)) {
            throw new ConflictException("Impossible de supprimer cet utilisateur : il possède des véhicules");
        }
        utilisateurRepository.delete(utilisateur);
    }

    private Utilisateur trouver(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id " + id));
    }
}
