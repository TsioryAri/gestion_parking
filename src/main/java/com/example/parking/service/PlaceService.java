package com.example.parking.service;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;

import java.util.List;

public interface PlaceService {
    PlaceResponse creerPlace(PlaceRequest request);
    List<PlaceResponse> listerPlaces();
    List<PlaceResponse> listerPlacesDisponibles();
    PlaceResponse obtenirPlace(Long id);
}
