package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.List;

@Service
public class AdminService {

    @Autowired private VilleRepository villeRepository;
    @Autowired private TrainRepository trainRepository;
    @Autowired private SegmentTrajetRepository segmentRepository;
    @Autowired private ValidationRepository validationRepository;
    @Autowired private VoyageurRepository voyageurRepository;

    public Ville ajouterVille(String nom) {
        if (villeRepository.existsByNom(nom))
            throw new RuntimeException("Ville déjà existante : " + nom);
        Ville ville = new Ville();
        ville.setNom(nom);
        return villeRepository.save(ville);
    }

    public Train ajouterTrain(String numero) {
        if (trainRepository.existsByNumero(numero))
            throw new RuntimeException("Train déjà existant : " + numero);
        Train train = new Train();
        train.setNumero(numero);
        return trainRepository.save(train);
    }

    public SegmentTrajet ajouterSegment(String nomDepart, String nomArrivee,
                                         String numeroTrain, LocalDate date,
                                         LocalTime heureDepart, LocalTime heureArrivee) {
        Ville depart = villeRepository.findByNom(nomDepart)
                .orElseThrow(() -> new RuntimeException("Ville départ introuvable"));
        Ville arrivee = villeRepository.findByNom(nomArrivee)
                .orElseThrow(() -> new RuntimeException("Ville arrivée introuvable"));
        Train train = trainRepository.findByNumero(numeroTrain)
                .orElseThrow(() -> new RuntimeException("Train introuvable"));

        SegmentTrajet segment = new SegmentTrajet();
        segment.setVilleDepart(depart);
        segment.setVilleArrivee(arrivee);
        segment.setTrain(train);
        segment.setDateDepart(date);
        segment.setHeureDepart(heureDepart);
        segment.setHeureArrivee(heureArrivee);
        return segmentRepository.save(segment);
    }

        public List<Validation> consulterTracabilite(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }

    public List<Ville> getToutesLesVilles() {
        return villeRepository.findAll();
    }

    public List<Train> getTousLesTrains() {
        return trainRepository.findAll();
    }

    public List<Voyageur> getTousLesVoyageurs() {
        return voyageurRepository.findAll();
    }

    public List<Validation> getToutesLesValidations() {
        return validationRepository.findAll();
    }
    public void supprimerVille(Long id) {
    villeRepository.deleteById(id);
    }
}