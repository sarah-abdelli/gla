package com.ticketeer.repository;

import com.ticketeer.entity.Billet;
import com.ticketeer.enums.EtatBillet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BilletRepository extends JpaRepository<Billet, String> {
    List<Billet> findByVoyageurId(Long voyageurId);
    Optional<Billet> findByVoyageurIdAndItineraireIdAndEtat(Long voyageurId, Long itineraireId, EtatBillet etat);
}