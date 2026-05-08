package com.ticketeer.repository;

import com.ticketeer.entity.SegmentTrajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SegmentTrajetRepository extends JpaRepository<SegmentTrajet, Long> {
    List<SegmentTrajet> findByVilleDepartNom(String nomVille);
    List<SegmentTrajet> findByVilleArriveeNom(String nomVille);
}
