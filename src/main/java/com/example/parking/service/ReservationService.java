package com.example.parking.service;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse creerReservation(ReservationRequest request);
    List<ReservationResponse> listerReservations();
    ReservationResponse obtenirReservation(Long id);
}
