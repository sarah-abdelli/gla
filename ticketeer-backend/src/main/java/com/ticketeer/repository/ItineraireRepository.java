package com.ticketeer.repository;

import com.ticketeer.entity.Itineraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItineraireRepository extends JpaRepository<Itineraire, Long> {
    @Query("SELECT i FROM Itineraire i JOIN i.segments s " +
           "WHERE s.villeDepart.nom = :depart AND s.villeArrivee.nom = :arrivee " +
           "AND SIZE(i.segments) = 1")
    List<Itineraire> findDirects(@Param("depart") String depart,
                                  @Param("arrivee") String arrivee);
}
