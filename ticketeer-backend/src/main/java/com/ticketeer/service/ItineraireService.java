package com.ticketeer.service;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.repository.ItineraireRepository;
import com.ticketeer.repository.SegmentTrajetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItineraireService {

    @Autowired
    private ItineraireRepository itineraireRepository;

    @Autowired
    private SegmentTrajetRepository segmentTrajetRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee, LocalDate date) {
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee, date);
        List<Itineraire> resultats = directs.isEmpty()
                ? itineraireRepository.findCorrespondances(depart, arrivee, date)
                : directs;

        if (date.equals(LocalDate.now())) {
            LocalTime maintenant = LocalTime.now();
            resultats = resultats.stream()
                    .filter(i -> i.getSegments().get(0).getHeureDepart().isAfter(maintenant))
                    .collect(Collectors.toList());
        }
        return resultats;
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire != null && itineraire.estCompatible();
    }
}