package com.example.parking.repository;

import com.example.parking.entity.Place;
import com.example.parking.entity.StatutPlace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByNumero(String numero);
    List<Place> findAllByOrderByZoneAscNumeroAsc();
    List<Place> findByStatutOrderByZoneAscNumeroAsc(StatutPlace statut);
    long countByStatut(StatutPlace statut);

    // Verrou en écriture sur la ligne de la place : deux réservations simultanées de la même place
    // sont traitées l'une après l'autre, ce qui empêche la double réservation d'un même créneau.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Place p where p.id = :id")
    Optional<Place> findByIdForUpdate(@Param("id") Long id);
}
