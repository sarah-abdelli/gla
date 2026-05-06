package com.ticketeer.repository;

import com.ticketeer.entity.Itineraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItineraireRepository extends JpaRepository<Itineraire, Long> {

    // Trajets directs : 1 seul segment, départ et arrivée correspondent
    @Query("SELECT i FROM Itineraire i JOIN i.segments s " +
            "WHERE SIZE(i.segments) = 1 " +
            "AND s.villeDepart.nom = :depart " +
            "AND s.villeArrivee.nom = :arrivee")
    List<Itineraire> findDirects(@Param("depart") String depart,
                                 @Param("arrivee") String arrivee);

    // Correspondances : premier segment part de depart, dernier arrive à arrivee
    @Query("SELECT DISTINCT i FROM Itineraire i " +
            "JOIN i.segments s1 JOIN i.segments s2 " +
            "WHERE SIZE(i.segments) > 1 " +
            "AND s1.villeDepart.nom = :depart " +
            "AND s2.villeArrivee.nom = :arrivee")
    List<Itineraire> findCorrespondances(@Param("depart") String depart,
                                         @Param("arrivee") String arrivee);
}