package com.example.parking.repository;

import com.example.parking.entity.Reservation;
import com.example.parking.entity.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Deux créneaux se chevauchent si arrivée(existante) < départ(nouvelle) ET départ(existante) > arrivée(nouvelle).
    // Inégalités strictes : un créneau qui commence pile à la fin d'un autre n'est PAS un chevauchement.
    @Query("""
            select count(r) from Reservation r
            where r.place.id = :placeId
              and r.statut in (:statuts)
              and r.arriveePrevue < :depart
              and r.departPrevu > :arrivee
            """)
    long compterChevauchements(@Param("placeId") Long placeId,
                               @Param("arrivee") LocalDateTime arrivee,
                               @Param("depart") LocalDateTime depart,
                               @Param("statuts") Collection<StatutReservation> statuts);
}
