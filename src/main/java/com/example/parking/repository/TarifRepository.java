package com.example.parking.repository;

import com.example.parking.entity.Tarif;
import com.example.parking.entity.TypeVehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarifRepository extends JpaRepository<Tarif, Long> {
    Optional<Tarif> findByZoneAndTypeVehicule(String zone, TypeVehicule typeVehicule);
}
