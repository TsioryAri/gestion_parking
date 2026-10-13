package com.example.parking.dto.response;

import com.example.parking.entity.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private Long vehiculeId;
    private String immatriculation;
    private Long placeId;
    private String placeNumero;
    private String placeZone;
    private LocalDateTime arriveePrevue;
    private LocalDateTime departPrevu;
    private StatutReservation statut;
}
