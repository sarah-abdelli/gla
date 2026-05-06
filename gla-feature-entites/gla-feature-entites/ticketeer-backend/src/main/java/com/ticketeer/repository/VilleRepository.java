package com.ticketeer.repository;

import com.ticketeer.entity.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {
    Optional<Ville> findByNom(String nom);
    boolean existsByNom(String nom);
}
