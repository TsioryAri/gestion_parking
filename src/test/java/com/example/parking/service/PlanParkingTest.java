package com.example.parking.service;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.response.PlaceResponse;
import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.TypePlace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlanParkingTest {

    @Autowired
    private PlaceService placeService;

    private PlaceRequest requete(String numero, String zone, TypePlace type, StatutPlace statut) {
        PlaceRequest request = new PlaceRequest();
        request.setNumero(numero);
        request.setZone(zone);
        request.setType(type);
        request.setStatut(statut);
        return request;
    }

    @Test
    void obtenirPlanParZone_doitGrouperParZoneEtTrierParNumero() {
        String zone = "TEST-" + System.nanoTime();
        // créées volontairement dans le désordre
        placeService.creerPlace(requete(zone + "-02", zone, TypePlace.STANDARD, null));
        placeService.creerPlace(requete(zone + "-01", zone, TypePlace.VIP, null));

        Map<String, List<PlaceResponse>> plan = placeService.obtenirPlanParZone();

        assertThat(plan).containsKey(zone);
        assertThat(plan.get(zone)).extracting(PlaceResponse::getNumero)
                .containsExactly(zone + "-01", zone + "-02");
    }

    @Test
    void compterParStatut_doitRefleterLesNouvellesPlaces() {
        long occupeesAvant = placeService.compterParStatut().get(StatutPlace.OCCUPEE);

        placeService.creerPlace(requete("T-" + System.nanoTime(), "TEST", TypePlace.PMR, StatutPlace.OCCUPEE));

        long occupeesApres = placeService.compterParStatut().get(StatutPlace.OCCUPEE);
        assertThat(occupeesApres).isEqualTo(occupeesAvant + 1);
    }
}
