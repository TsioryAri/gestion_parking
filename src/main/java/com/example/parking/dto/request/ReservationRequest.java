package com.example.parking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequest {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehiculeId;

    @NotNull(message = "La place est obligatoire")
    private Long placeId;

    @NotNull(message = "L'arrivée prévue est obligatoire")
    private LocalDateTime arriveePrevue;

    @NotNull(message = "Le départ prévu est obligatoire")
    private LocalDateTime departPrevu;
}
