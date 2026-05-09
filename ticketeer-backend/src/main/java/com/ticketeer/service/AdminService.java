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

    public List<Ville> getToutesLesVilles() {
        return villeRepository.findAll();
    }

    public void supprimerVille(Long id) {
        if (!villeRepository.existsById(id))
            throw new RuntimeException("Ville introuvable : " + id);
        villeRepository.deleteById(id);
    }

    public Train ajouterTrain(String numero) {
        if (trainRepository.existsByNumero(numero))
            throw new RuntimeException("Train déjà existant : " + numero);
        Train train = new Train();
        train.setNumero(numero);
        return trainRepository.save(train);
    }

    public List<Train> getTousLesTrains() {
        return trainRepository.findAll();
    }

    public void supprimerTrain(Long id) {
        if (!trainRepository.existsById(id))
            throw new RuntimeException("Train introuvable : " + id);
        trainRepository.deleteById(id);
    }


    public SegmentTrajet ajouterSegment(String nomDepart, String nomArrivee,
                                        String numeroTrain, LocalDate date,
                                        LocalTime heureDepart, LocalTime heureArrivee) {
        if (!heureArrivee.isAfter(heureDepart))
            throw new RuntimeException("L'heure d'arrivée doit être après l'heure de départ");

        Ville depart = villeRepository.findByNom(nomDepart)
                .orElseThrow(() -> new RuntimeException("Ville départ introuvable : " + nomDepart));
        Ville arrivee = villeRepository.findByNom(nomArrivee)
                .orElseThrow(() -> new RuntimeException("Ville arrivée introuvable : " + nomArrivee));
        Train train = trainRepository.findByNumero(numeroTrain)
                .orElseThrow(() -> new RuntimeException("Train introuvable : " + numeroTrain));

        SegmentTrajet segment = new SegmentTrajet();
        segment.setVilleDepart(depart);
        segment.setVilleArrivee(arrivee);
        segment.setTrain(train);
        segment.setDateDepart(date);
        segment.setHeureDepart(heureDepart);
        segment.setHeureArrivee(heureArrivee);
        return segmentRepository.save(segment);
    }

    public List<SegmentTrajet> getTousLesSegments() {
        return segmentRepository.findAll();
    }

    public void supprimerSegment(Long id) {
        if (!segmentRepository.existsById(id))
            throw new RuntimeException("Segment introuvable : " + id);
        segmentRepository.deleteById(id);
    }


    public List<Voyageur> getTousLesVoyageurs() {
        return voyageurRepository.findAll();
    }

    public void supprimerVoyageur(Long id) {
        if (!voyageurRepository.existsById(id))
            throw new RuntimeException("Voyageur introuvable : " + id);
        voyageurRepository.deleteById(id);
    }

    public Voyageur modifierVoyageur(Long id, String nom, String email) {
        Voyageur voyageur = voyageurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voyageur introuvable : " + id));
        if (nom != null && !nom.isBlank())
            voyageur.setNom(nom);
        if (email != null && !email.isBlank())
            voyageur.setEmail(email);
        return voyageurRepository.save(voyageur);
    }


    public List<Validation> consulterTracabilite(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }

    public List<Validation> getToutesLesValidations() {
        return validationRepository.findAll();
    }
}