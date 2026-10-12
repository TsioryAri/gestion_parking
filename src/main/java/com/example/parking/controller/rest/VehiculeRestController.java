package com.example.parking.controller.rest;

import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.VehiculeResponse;
import com.example.parking.service.VehiculeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehiculeRestController {

    private final VehiculeService vehiculeService;

    public VehiculeRestController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> creer(@Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.creerVehicule(request));
    }

    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> lister() {
        return ResponseEntity.ok(vehiculeService.listerVehicules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.obtenirVehicule(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> modifier(@PathVariable Long id,
                                                     @Valid @RequestBody VehiculeRequest request) {
        return ResponseEntity.ok(vehiculeService.modifierVehicule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        vehiculeService.supprimerVehicule(id);
        return ResponseEntity.noContent().build();
    }
}
