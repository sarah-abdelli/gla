package com.ticketeer.service;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.repository.ItineraireRepository;
import com.ticketeer.repository.SegmentTrajetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItineraireService {

    @Autowired private ItineraireRepository itineraireRepository;
    @Autowired private SegmentTrajetRepository segmentTrajetRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee) {
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee);
        if (!directs.isEmpty()) return directs;
        return rechercherCorrespondances(depart, arrivee);
    }

    private List<Itineraire> rechercherCorrespondances(String depart, String arrivee) {
        // TODO : implémenter la logique de correspondance
        return List.of();
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire.estCompatible();
    }
}
