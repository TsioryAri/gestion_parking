package com.example.parking.mapper;

import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUtilisateur().getId(),
                reservation.getUtilisateur().getNom(),
                reservation.getVehicule().getId(),
                reservation.getVehicule().getImmatriculation(),
                reservation.getPlace().getId(),
                reservation.getPlace().getNumero(),
                reservation.getPlace().getZone(),
                reservation.getArriveePrevue(),
                reservation.getDepartPrevu(),
                reservation.getStatut()
        );
    }
}
