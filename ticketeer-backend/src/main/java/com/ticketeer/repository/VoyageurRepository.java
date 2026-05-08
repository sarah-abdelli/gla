package com.ticketeer.repository;

import com.ticketeer.entity.Voyageur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoyageurRepository extends JpaRepository<Voyageur, Long> {
    Optional<Voyageur> findByEmail(String email);
}
