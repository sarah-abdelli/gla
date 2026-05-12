package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.enums.EtatBillet;
import com.ticketeer.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BilletService {

    @Autowired private BilletRepository billetRepository;
    @Autowired private VoyageurRepository voyageurRepository;
    @Autowired private ItineraireRepository itineraireRepository;

    @Transactional
    public Billet creerBillet(Long voyageurId, Long itineraireId) {
        Voyageur voyageur = voyageurRepository.findById(voyageurId)
                .orElseThrow(() -> new RuntimeException("Voyageur introuvable"));
        Itineraire itineraire = itineraireRepository.findById(itineraireId)
                .orElseThrow(() -> new RuntimeException("Itinéraire introuvable"));

        // Vérifier si un billet VALIDE existe déjà pour ce voyageur et cet itinéraire
        Optional<Billet> existant = billetRepository
                .findByVoyageurIdAndItineraireIdAndEtat(voyageurId, itineraireId, EtatBillet.VALIDE);

        if (existant.isPresent()) {
            // Retourner le billet existant au lieu d'en créer un nouveau
            return existant.get();
        }

        // Créer un nouveau billet
        Billet billet = new Billet();
        billet.setUuid(java.util.UUID.randomUUID().toString());
        billet.setDateCreation(LocalDateTime.now());
        billet.setEtat(EtatBillet.VALIDE);
        billet.setVoyageur(voyageur);
        billet.setItineraire(itineraire);

        return billetRepository.save(billet);
    }

    public Billet getBillet(String uuid) {
        return billetRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Billet introuvable : " + uuid));
    }

    public List<Billet> getBilletsByVoyageur(Long voyageurId) {
        return billetRepository.findByVoyageurId(voyageurId);
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