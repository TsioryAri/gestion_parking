package com.example.parking.mapper;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.Place;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

    public Place toEntity(PlaceRequest request) {
        Place place = new Place();
        place.setNumero(request.getNumero().trim());
        place.setZone(request.getZone().trim());
        place.setType(request.getType());
        if (request.getStatut() != null) {
            place.setStatut(request.getStatut());
        }
        return place;
    }

    public PlaceResponse toResponse(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getNumero(),
                place.getZone(),
                place.getType(),
                place.getStatut()
        );
    }
}
