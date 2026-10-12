package com.example.parking.repository;

import com.example.parking.entity.Place;
import com.example.parking.entity.StatutPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    boolean existsByNumero(String numero);
    List<Place> findAllByOrderByZoneAscNumeroAsc();
    List<Place> findByStatutOrderByZoneAscNumeroAsc(StatutPlace statut);
    long countByStatut(StatutPlace statut);
}
