package com.example.parking.dto.response;

import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.TypePlace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    private Long id;
    private String numero;
    private String zone;
    private TypePlace type;
    private StatutPlace statut;
}
