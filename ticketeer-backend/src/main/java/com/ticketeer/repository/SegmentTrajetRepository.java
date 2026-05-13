package com.ticketeer.repository;

import com.ticketeer.entity.SegmentTrajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SegmentTrajetRepository extends JpaRepository<SegmentTrajet, Long> {

    List<SegmentTrajet> findByVilleDepartNom(String nomVille);

    List<SegmentTrajet> findByVilleArriveeNom(String nomVille);

    List<SegmentTrajet> findByVilleDepartIdOrVilleArriveeId(Long departId, Long arriveeId);

    @Query("SELECT DISTINCT s.train.id FROM SegmentTrajet s WHERE s.dateDepart = :date")
    List<Long> findTrainIdsUtilisesParDate(@Param("date") LocalDate date);

    // Nouveau : récupérer uniquement les segments d'une date précise
    List<SegmentTrajet> findByDateDepart(LocalDate dateDepart);
}