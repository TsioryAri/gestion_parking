package com.example.parking.service.impl;

import com.example.parking.dto.request.ReservationRequest;
import com.example.parking.dto.response.ReservationResponse;
import com.example.parking.entity.Place;
import com.example.parking.entity.Reservation;
import com.example.parking.entity.StatutPlace;
import com.example.parking.entity.StatutReservation;
import com.example.parking.entity.Utilisateur;
import com.example.parking.entity.Vehicule;
import com.example.parking.exception.BusinessException;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.InvalidOperationException;
import com.example.parking.exception.ResourceNotFoundException;
import com.example.parking.mapper.ReservationMapper;
import com.example.parking.repository.PlaceRepository;
import com.example.parking.repository.ReservationRepository;
import com.example.parking.repository.UtilisateurRepository;
import com.example.parking.repository.VehiculeRepository;
import com.example.parking.service.ReservationService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    // Un formulaire envoyé à 10:00:30 pour une arrivée à 10:00 ne doit pas être refusé
    private static final long TOLERANCE_PASSE_MINUTES = 1;

    // Seules ces réservations bloquent un créneau
    private static final List<StatutReservation> STATUTS_ACTIFS =
            List.of(StatutReservation.CONFIRMEE, StatutReservation.EN_COURS);

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final PlaceRepository placeRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  UtilisateurRepository utilisateurRepository,
                                  VehiculeRepository vehiculeRepository,
                                  PlaceRepository placeRepository,
                                  ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.placeRepository = placeRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    @Transactional
    public ReservationResponse creerReservation(ReservationRequest request) {
        LocalDateTime arrivee = request.getArriveePrevue();
        LocalDateTime depart = request.getDepartPrevu();

        // 1. Validation du créneau
        if (!depart.isAfter(arrivee)) {
            throw new BusinessException("L'heure de départ doit être postérieure à l'heure d'arrivée");
        }
        if (arrivee.isBefore(LocalDateTime.now().minusMinutes(TOLERANCE_PASSE_MINUTES))) {
            throw new BusinessException("L'arrivée prévue ne peut pas être dans le passé");
        }

        // 2. Existence des ressources et cohérence utilisateur / véhicule
        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'id " + request.getUtilisateurId()));
        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Véhicule introuvable avec l'id " + request.getVehiculeId()));
        if (!vehicule.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new BusinessException("Ce véhicule n'appartient pas à cet utilisateur");
        }

        // 3. Place : verrouillée pour toute la durée de la transaction
        Place place = placeRepository.findByIdForUpdate(request.getPlaceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Place introuvable avec l'id " + request.getPlaceId()));
        if (place.getStatut() == StatutPlace.HORS_SERVICE) {
            throw new InvalidOperationException("La place " + place.getNumero() + " est hors service");
        }

        // 4. Disponibilité sur le créneau demandé
        if (reservationRepository.compterChevauchements(place.getId(), arrivee, depart, STATUTS_ACTIFS) > 0) {
            throw new ConflictException("La place " + place.getNumero() + " est déjà réservée sur ce créneau");
        }

        // 5. Création de la réservation et changement de statut de la place (même transaction)
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setVehicule(vehicule);
        reservation.setPlace(place);
        reservation.setArriveePrevue(arrivee);
        reservation.setDepartPrevu(depart);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationRepository.save(reservation);

        // Une place occupée en ce moment le reste ; seule une place LIBRE devient RESERVEE
        if (place.getStatut() == StatutPlace.LIBRE) {
            place.setStatut(StatutPlace.RESERVEE);
            placeRepository.save(place);
        }

        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> listerReservations() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "arriveePrevue")).stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse obtenirReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'id " + id));
        return reservationMapper.toResponse(reservation);
    }
}
