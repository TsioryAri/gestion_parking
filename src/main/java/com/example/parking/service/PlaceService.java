package com.example.parking.service;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.StatutPlace;

import java.util.List;
import java.util.Map;

public interface PlaceService {
    PlaceResponse creerPlace(PlaceRequest request);
    List<PlaceResponse> listerPlaces();
    List<PlaceResponse> listerPlacesDisponibles();
    PlaceResponse obtenirPlace(Long id);
    Map<String, List<PlaceResponse>> obtenirPlanParZone();
    Map<StatutPlace, Long> compterParStatut();
}
