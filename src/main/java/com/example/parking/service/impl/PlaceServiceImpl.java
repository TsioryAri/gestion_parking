package com.example.parking.service.impl;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.Place;
import com.example.parking.entity.StatutPlace;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.PlaceMapper;
import com.example.parking.repository.PlaceRepository;
import com.example.parking.service.PlaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    @Override
    @Transactional
    public PlaceResponse creerPlace(PlaceRequest request) {
        String numero = request.getNumero().trim();
        if (placeRepository.existsByNumero(numero)) {
            throw new ConflictException("Une place avec le numéro " + numero + " existe déjà");
        }
        Place place = placeRepository.save(placeMapper.toEntity(request));
        return placeMapper.toResponse(place);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> listerPlaces() {
        return placeRepository.findAllByOrderByZoneAscNumeroAsc().stream()
                .map(placeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> listerPlacesDisponibles() {
        return placeRepository.findByStatutOrderByZoneAscNumeroAsc(StatutPlace.LIBRE).stream()
                .map(placeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceResponse obtenirPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place introuvable avec l'id " + id));
        return placeMapper.toResponse(place);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<PlaceResponse>> obtenirPlanParZone() {
        Map<String, List<PlaceResponse>> plan = new LinkedHashMap<>();
        for (Place place : placeRepository.findAllByOrderByZoneAscNumeroAsc()) {
            plan.computeIfAbsent(place.getZone(), zone -> new ArrayList<>())
                    .add(placeMapper.toResponse(place));
        }
        return plan;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<StatutPlace, Long> compterParStatut() {
        Map<StatutPlace, Long> compteurs = new EnumMap<>(StatutPlace.class);
        for (StatutPlace statut : StatutPlace.values()) {
            compteurs.put(statut, placeRepository.countByStatut(statut));
        }
        return compteurs;
    }
}
