package com.example.parking.service;

import com.example.parking.dto.request.PlaceRequest;
import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.request.UtilisateurRequest;
import com.example.parking.dto.request.VehiculeRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.StatutReservation;
import com.example.parking.entity.TypePlace;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReservationServiceImplTest {

    @Autowired private ReservationService reservationService;
    @Autowired private UtilisateurService utilisateurService;
    @Autowired private VehiculeService vehiculeService;
    @Autowired private PlaceService placeService;

    // Demain à 10:00 : toujours dans le futur
    private final LocalDateTime base = LocalDateTime.now().plusDays(1)
            .withHour(10).withMinute(0).withSecond(0).withNano(0);

    private Long creerUtilisateur() {
        UtilisateurRequest request = new UtilisateurRequest();
        request.setNom("Test Reservation");
        request.setEmail("res-" + System.nanoTime() + "@example.com");
        return utilisateurService.creerUtilisateur(request).getId();
    }

    private Long creerVehicule(Long utilisateurId) {
        VehiculeRequest request = new VehiculeRequest();
        request.setImmatriculation("R-" + System.nanoTime());
        request.setUtilisateurId(utilisateurId);
        return vehiculeService.creerVehicule(request).getId();
    }

    private Long creerPlace(StatutPlace statut) {
        PlaceRequest request = new PlaceRequest();
        request.setNumero("T-" + System.nanoTime());
        request.setZone("TEST");
        request.setType(TypePlace.STANDARD);
        request.setStatut(statut);
        return placeService.creerPlace(request).getId();
    }

    private ReservationRequest requete(Long utilisateurId, Long vehiculeId, Long placeId,
                                       LocalDateTime arrivee, LocalDateTime depart) {
        ReservationRequest request = new ReservationRequest();
        request.setUtilisateurId(utilisateurId);
        request.setVehiculeId(vehiculeId);
        request.setPlaceId(placeId);
        request.setArriveePrevue(arrivee);
        request.setDepartPrevu(depart);
        return request;
    }

    @Test
    void reservationValide_doitEtreConfirmeeEtMarquerLaPlaceReservee() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(null);

        ReservationResponse reservation = reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base, base.plusHours(2)));

        assertThat(reservation.getId()).isNotNull();
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
        assertThat(placeService.obtenirPlace(place).getStatut()).isEqualTo(StatutPlace.RESERVEE);
    }

    @Test
    void chevauchement_doitLeverConflictException() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(null);
        reservationService.creerReservation(requete(utilisateur, vehicule, place, base, base.plusHours(2)));

        assertThatThrownBy(() -> reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base.plusHours(1), base.plusHours(3))))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("déjà réservée");
    }

    @Test
    void creneauxAdjacents_doiventEtreAcceptes() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(null);
        reservationService.creerReservation(requete(utilisateur, vehicule, place, base, base.plusHours(2)));

        // commence pile à la fin de la première, et finit pile au début de la première
        ReservationResponse apres = reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base.plusHours(2), base.plusHours(4)));
        ReservationResponse avant = reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base.minusHours(2), base));

        assertThat(apres.getId()).isNotNull();
        assertThat(avant.getId()).isNotNull();
    }

    @Test
    void departAvantArrivee_doitLeverBusinessException() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(null);

        assertThatThrownBy(() -> reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base.plusHours(2), base)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("postérieure");
    }

    @Test
    void arriveeDansLePasse_doitLeverBusinessException() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(null);
        LocalDateTime hier = LocalDateTime.now().minusDays(1);

        assertThatThrownBy(() -> reservationService.creerReservation(
                requete(utilisateur, vehicule, place, hier, hier.plusHours(2))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("passé");
    }

    @Test
    void vehiculeDunAutreUtilisateur_doitLeverBusinessException() {
        Long utilisateur = creerUtilisateur();
        Long autreUtilisateur = creerUtilisateur();
        Long vehiculeDeLautre = creerVehicule(autreUtilisateur);
        Long place = creerPlace(null);

        assertThatThrownBy(() -> reservationService.creerReservation(
                requete(utilisateur, vehiculeDeLautre, place, base, base.plusHours(2))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("n'appartient pas");
    }

    @Test
    void placeHorsService_doitLeverInvalidOperationException() {
        Long utilisateur = creerUtilisateur();
        Long vehicule = creerVehicule(utilisateur);
        Long place = creerPlace(StatutPlace.HORS_SERVICE);

        assertThatThrownBy(() -> reservationService.creerReservation(
                requete(utilisateur, vehicule, place, base, base.plusHours(2))))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("hors service");
    }
}
