package com.ticketeer.service;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.repository.ItineraireRepository;
import com.ticketeer.repository.SegmentTrajetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ItineraireService {

    @Autowired
    private ItineraireRepository itineraireRepository;

    @Autowired
    private SegmentTrajetRepository segmentTrajetRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee, LocalDate date) {
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee, date);
        if (!directs.isEmpty()) return directs;
        return itineraireRepository.findCorrespondances(depart, arrivee, date);
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire != null && itineraire.estCompatible();
    }
}