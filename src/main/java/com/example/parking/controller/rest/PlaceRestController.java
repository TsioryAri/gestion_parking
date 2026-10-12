package com.example.parking.controller.rest;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/spaces")
public class PlaceRestController {

    private final PlaceService placeService;

    public PlaceRestController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> creerPlace(@Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.creerPlace(request));
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> listerPlaces() {
        return ResponseEntity.ok(placeService.listerPlaces());
    }

    @GetMapping("/available")
    public ResponseEntity<List<PlaceResponse>> listerPlacesDisponibles() {
        return ResponseEntity.ok(placeService.listerPlacesDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> obtenirPlace(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.obtenirPlace(id));
    }
}
