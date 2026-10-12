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

import java.util.List;

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
        return placeRepository.findAll().stream()
                .map(placeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaceResponse> listerPlacesDisponibles() {
        return placeRepository.findByStatut(StatutPlace.LIBRE).stream()
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
}
