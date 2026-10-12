package com.example.parking.service;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.TypePlace;
import com.example.parking.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PlaceServiceImplTest {

    @Autowired
    private PlaceService placeService;

    private PlaceRequest requete(String numero, TypePlace type, StatutPlace statut) {
        PlaceRequest request = new PlaceRequest();
        request.setNumero(numero);
        request.setZone("A");
        request.setType(type);
        request.setStatut(statut);
        return request;
    }

    private String numeroUnique() {
        return "T-" + System.nanoTime();
    }

    @Test
    void creerPlace_doitRetournerUnePlaceLibreParDefaut() {
        String numero = numeroUnique();

        PlaceResponse response = placeService.creerPlace(requete(numero, TypePlace.VIP, null));

        assertThat(response.getId()).isNotNull();
        assertThat(response.getNumero()).isEqualTo(numero);
        assertThat(response.getZone()).isEqualTo("A");
        assertThat(response.getType()).isEqualTo(TypePlace.VIP);
        assertThat(response.getStatut()).isEqualTo(StatutPlace.LIBRE);
    }

    @Test
    void creerPlace_numeroDejaUtilise_doitLeverConflictException() {
        String numero = numeroUnique();
        placeService.creerPlace(requete(numero, TypePlace.STANDARD, null));

        assertThatThrownBy(() -> placeService.creerPlace(requete(numero, TypePlace.PMR, null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(numero);
    }

    @Test
    void listerPlacesDisponibles_neDoitContenirQueDesPlacesLibres() {
        String numeroLibre = numeroUnique();
        String numeroHorsService = numeroUnique() + "-HS";
        placeService.creerPlace(requete(numeroLibre, TypePlace.STANDARD, null));
        placeService.creerPlace(requete(numeroHorsService, TypePlace.STANDARD, StatutPlace.HORS_SERVICE));

        List<PlaceResponse> disponibles = placeService.listerPlacesDisponibles();

        assertThat(disponibles).allMatch(p -> p.getStatut() == StatutPlace.LIBRE);
        assertThat(disponibles).anyMatch(p -> p.getNumero().equals(numeroLibre));
        assertThat(disponibles).noneMatch(p -> p.getNumero().equals(numeroHorsService));
    }
}
