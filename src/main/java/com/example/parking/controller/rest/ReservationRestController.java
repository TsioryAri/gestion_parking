package com.example.parking.controller.rest;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {

    private final ReservationService reservationService;

    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> creer(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creerReservation(request));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> lister() {
        return ResponseEntity.ok(reservationService.listerReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.obtenirReservation(id));
    }
}
