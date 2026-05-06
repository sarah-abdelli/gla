package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.enums.ResultatValidation;
import com.ticketeer.repository.BilletRepository;
import com.ticketeer.repository.ValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class ValidationService {

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private TokenService tokenService;

    public Validation validerBillet(String uuid, String tokenValeur,
                                    String numeroTrain, LocalDate date, LocalTime heure) {

<<<<<<< HEAD
=======
        // 1. Vérifier le token de l'agent
>>>>>>> origin/feature/entites
        AgentControle agent = tokenService.getAgentParToken(tokenValeur);
        if (agent == null) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("Token invalide ou absent");
            return v; // retourne sans sauvegarder
        }

        // 2. Vérifier que le billet existe
        Billet billet = billetRepository.findByUuid(uuid).orElse(null);
        if (billet == null) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("UUID inconnu");
            v.setAgent(agent);
            return v; // retourne sans sauvegarder
        }

        // 3. Vérifier l'état du billet
        if (!billet.estValide()) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("Billet déjà utilisé ou invalide");
            v.setBillet(billet);
            v.setAgent(agent);
            try {
                return validationRepository.save(v);
            } catch (Exception e) {
                return v;
            }
        }

<<<<<<< HEAD
        if (!verifierSegment(billet, numeroTrain)) {
            return enregistrerRefus(uuid, agent, "Hors itinéraire - mauvais train");
        }

        if (!verifierContexte(billet, numeroTrain, date, heure)) {
            return enregistrerRefus(uuid, agent, "Mauvaise date ou heure");
        }

        if (verifierDernierSegment(billet, numeroTrain)) {
            billet.marquerUtilise();
            billetRepository.save(billet);
        }
=======
        // 4. Marquer le billet utilisé
        billet.marquerUtilise();
        billetRepository.save(billet);
>>>>>>> origin/feature/entites

        // 5. Enregistrer la validation ACCEPTEE
        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());
        validation.setResultat(ResultatValidation.ACCEPTEE);
        validation.setBillet(billet);
        validation.setAgent(agent);
<<<<<<< HEAD

        return validationRepository.save(validation);
    }

    private boolean verifierSegment(Billet billet, String numeroTrain) {
        return billet.getItineraire().getSegments().stream()
                .anyMatch(s -> s.getTrain().getNumero().equals(numeroTrain));
    }

    private boolean verifierContexte(Billet billet, String numeroTrain,
                                     LocalDate date, LocalTime heure) {
        return billet.getItineraire().getSegments().stream()
                .filter(s -> s.getTrain().getNumero().equals(numeroTrain))
                .anyMatch(s ->
                        s.getDateDepart().equals(date)
                                && s.estDansLaBonnePlage(heure)
                );
    }

    private boolean verifierDernierSegment(Billet billet, String numeroTrain) {
        List<SegmentTrajet> segments = billet.getItineraire().getSegments();

        if (segments == null || segments.isEmpty()) {
            return false;
        }

        SegmentTrajet dernier = segments.get(segments.size() - 1);
        return dernier.getTrain().getNumero().equals(numeroTrain);
    }

    private Validation enregistrerRefus(String uuid, AgentControle agent, String motif) {
        Validation v = new Validation();
        v.setDateHeure(LocalDateTime.now());
        v.setResultat(ResultatValidation.REFUSEE);
        v.setMotifRefus(motif);
        v.setAgent(agent);

        if (uuid != null) {
            billetRepository.findByUuid(uuid).ifPresent(v::setBillet);
        }

        return validationRepository.save(v);
=======
        try {
            return validationRepository.save(validation);
        } catch (Exception e) {
            return validation;
        }
>>>>>>> origin/feature/entites
    }

    public List<Validation> getHistorique(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }
}