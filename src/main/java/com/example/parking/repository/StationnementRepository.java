package com.example.parking.repository;

import com.example.parking.entity.Stationnement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationnementRepository extends JpaRepository<Stationnement, Long> {
}
