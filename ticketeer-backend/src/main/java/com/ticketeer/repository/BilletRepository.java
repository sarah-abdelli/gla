package com.ticketeer.repository;

import com.ticketeer.entity.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BilletRepository extends JpaRepository<Billet, String> {
    List<Billet> findByVoyageurId(Long voyageurId);
}