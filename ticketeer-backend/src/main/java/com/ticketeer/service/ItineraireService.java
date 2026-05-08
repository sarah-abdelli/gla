package com.ticketeer.service;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.entity.SegmentTrajet;
import com.ticketeer.repository.ItineraireRepository;
import com.ticketeer.repository.SegmentTrajetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItineraireService {

    @Autowired
    private ItineraireRepository itineraireRepository;

    @Autowired
    private SegmentTrajetRepository segmentTrajetRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee) {
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee);

        if (!directs.isEmpty()) {
            return directs;
        }

        return rechercherCorrespondances(depart, arrivee);
    }

    private List<Itineraire> rechercherCorrespondances(String depart, String arrivee) {
        List<Itineraire> resultats = new ArrayList<>();

        List<SegmentTrajet> premiersSegments =
                segmentTrajetRepository.findByVilleDepartNom(depart);

        List<SegmentTrajet> deuxiemesSegments =
                segmentTrajetRepository.findByVilleArriveeNom(arrivee);

        for (SegmentTrajet premier : premiersSegments) {
            for (SegmentTrajet deuxieme : deuxiemesSegments) {

                boolean memeVilleCorrespondance =
                        premier.getVilleArrivee().getNom()
                                .equalsIgnoreCase(deuxieme.getVilleDepart().getNom());

                boolean horairesCompatibles =
                        premier.getHeureArrivee().isBefore(deuxieme.getHeureDepart());

                if (memeVilleCorrespondance && horairesCompatibles) {
                    Itineraire itineraire = new Itineraire();
                    itineraire.setSegments(List.of(premier, deuxieme));
                    resultats.add(itineraire);
                }
            }
        }

        return resultats;
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire != null && itineraire.estCompatible();
    }
}