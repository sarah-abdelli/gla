package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BilletService {

    @Autowired private BilletRepository billetRepository;
    @Autowired private VoyageurRepository voyageurRepository;
    @Autowired private ItineraireRepository itineraireRepository;

    public Billet creerBillet(Long voyageurId, Long itineraireId) {
        Voyageur voyageur = voyageurRepository.findById(voyageurId)
                .orElseThrow(() -> new RuntimeException("Voyageur introuvable"));
        Itineraire itineraire = itineraireRepository.findById(itineraireId)
                .orElseThrow(() -> new RuntimeException("Itinéraire introuvable"));

        Billet billet = Billet.creer(voyageur, itineraire);
        return billetRepository.save(billet);
    }

    public Billet getBillet(String uuid) {
        return billetRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Billet introuvable : " + uuid));
    }

    public void invaliderBillet(String uuid) {
        Billet billet = getBillet(uuid);
        billet.invalider();
        billetRepository.save(billet);
    }

    public String getQRData(String uuid) {
        getBillet(uuid);
        return uuid;
    }
}