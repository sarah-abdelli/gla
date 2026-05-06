package com.ticketeer.service;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.repository.ItineraireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItineraireService {

    @Autowired private ItineraireRepository itineraireRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee) {
        List<Itineraire> resultats = new ArrayList<>();

        // 1. Cherche les trajets directs
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee);
        resultats.addAll(directs);

        // 2. Cherche les correspondances
        List<Itineraire> correspondances = itineraireRepository.findCorrespondances(depart, arrivee);

        // Filtre : garde uniquement les correspondances valides (horaires compatibles)
        for (Itineraire itin : correspondances) {
            if (itin.estCompatible()) {
                // Vérifie que le premier segment part bien de depart
                // et le dernier arrive bien à arrivee
                String villeDepart = itin.getVilleDepart() != null ?
                        itin.getVilleDepart().getNom() : "";
                String villeArrivee = itin.getVilleArrivee() != null ?
                        itin.getVilleArrivee().getNom() : "";

                if (villeDepart.equals(depart) && villeArrivee.equals(arrivee)) {
                    resultats.add(itin);
                }
            }
        }

        return resultats;
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire.estCompatible();
    }
}