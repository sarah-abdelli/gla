package com.ticketeer.repository;

import com.ticketeer.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

// ---- Billet ----
interface BilletRepository extends JpaRepository<Billet, String> {
    Optional<Billet> findByUuid(String uuid);
    List<Billet> findByVoyageurId(Long voyageurId);
}

// ---- Voyageur ----
interface VoyageurRepository extends JpaRepository<Voyageur, Long> {
    Optional<Voyageur> findByEmail(String email);
}

// ---- AgentControle ----
interface AgentControleRepository extends JpaRepository<AgentControle, Long> {
    Optional<AgentControle> findByLogin(String login);
}

// ---- Itineraire ----
interface ItineraireRepository extends JpaRepository<Itineraire, Long> {
    // Recherche itinéraires directs entre deux villes
    @Query("SELECT i FROM Itineraire i JOIN i.segments s " +
           "WHERE s.villeDepart.nom = :depart AND s.villeArrivee.nom = :arrivee " +
           "AND SIZE(i.segments) = 1")
    List<Itineraire> findDirects(@Param("depart") String depart,
                                  @Param("arrivee") String arrivee);
}

// ---- SegmentTrajet ----
interface SegmentTrajetRepository extends JpaRepository<SegmentTrajet, Long> {
    List<SegmentTrajet> findByVilleDepartNom(String nomVille);
    List<SegmentTrajet> findByVilleArriveeNom(String nomVille);
}

// ---- Ville ----
interface VilleRepository extends JpaRepository<Ville, Long> {
    Optional<Ville> findByNom(String nom);
    boolean existsByNom(String nom);
}

// ---- Train ----
interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByNumero(String numero);
    boolean existsByNumero(String numero);
}

// ---- Validation ----
interface ValidationRepository extends JpaRepository<Validation, Long> {
    List<Validation> findByBilletUuid(String uuid);
}

// ---- Token ----
interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValeur(String valeur);
}
