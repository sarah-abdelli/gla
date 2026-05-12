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

    @Autowired private BilletRepository billetRepository;
    @Autowired private ValidationRepository validationRepository;
    @Autowired private TokenService tokenService;

    public Validation validerBillet(String uuid, String tokenValeur,
                                    String numeroTrain, LocalDate date, LocalTime heure) {

        // 1. Vérifier le token de l'agent
        AgentControle agent = tokenService.getAgentParToken(tokenValeur);
        if (agent == null) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("Token invalide ou absent");
            return v;
        }

        // 2. Vérifier que le billet existe
        Billet billet = billetRepository.findById(uuid).orElse(null);
        if (billet == null) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("UUID inconnu");
            v.setAgent(agent);
            return v;
        }

        // 3. Vérifier l'état du billet
        if (!billet.estValide()) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("Billet déjà utilisé ou invalide");
            v.setBillet(billet);
            v.setAgent(agent);
            try { return validationRepository.save(v); } catch (Exception e) { return v; }
        }

        // 4. Vérifier que le train correspond à l'itinéraire
        if (numeroTrain != null && !numeroTrain.isEmpty()) {
            if (!verifierSegment(billet, numeroTrain)) {
                return enregistrerRefus(billet, agent, "Mauvais train - ce billet n'est pas valable sur ce train");
            }

            // 5. Vérifier date et heure
            if (!verifierContexte(billet, numeroTrain, date, heure)) {
                return enregistrerRefus(billet, agent, "Mauvaise date ou heure de voyage");
            }

            // 6. Anti-fraude : vérifier si ce train a déjà été validé pour ce billet
            boolean dejaValide = validationRepository
                    .findByBilletUuid(uuid)
                    .stream()
                    .filter(v -> v.getResultat() == ResultatValidation.ACCEPTEE)
                    .anyMatch(v -> numeroTrain.equals(v.getNumeroTrain()));

            if (dejaValide) {
                return enregistrerRefus(billet, agent, "Ce segment a déjà été validé");
            }
        }

        // 7. Enregistrer d'abord la validation ACCEPTEE avec le numéro du train
        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());
        validation.setResultat(ResultatValidation.ACCEPTEE);
        validation.setBillet(billet);
        validation.setAgent(agent);
        validation.setNumeroTrain(numeroTrain);
        try {
            validation = validationRepository.save(validation);
        } catch (Exception e) {
            // On continue même si la sauvegarde échoue
        }

        // 8. Marquer le billet UTILISÉ seulement si TOUS les segments ont été validés
        //    (on compte les validations ACCEPTEE en base + celle qu'on vient d'enregistrer)
        if (numeroTrain == null || numeroTrain.isEmpty()) {
            // Pas de numéro de train fourni : on marque directement UTILISÉ
            billet.marquerUtilise();
            billetRepository.save(billet);
        } else {
            long nbSegments = billet.getItineraire().getSegments().size();
            long nbValidationsAcceptees = validationRepository
                    .findByBilletUuid(uuid)
                    .stream()
                    .filter(v -> v.getResultat() == ResultatValidation.ACCEPTEE)
                    .count();

            if (nbValidationsAcceptees >= nbSegments) {
                billet.marquerUtilise();
                billetRepository.save(billet);
            }
        }

        return validation;
    }

    private boolean verifierSegment(Billet billet, String numeroTrain) {
        return billet.getItineraire().getSegments().stream()
                .anyMatch(s -> s.getTrain().getNumero().equals(numeroTrain));
    }

    private boolean verifierContexte(Billet billet, String numeroTrain,
                                     LocalDate date, LocalTime heure) {
        return billet.getItineraire().getSegments().stream()
                .filter(s -> s.getTrain().getNumero().equals(numeroTrain))
                .anyMatch(s -> s.getDateDepart().equals(date) && s.estDansLaBonnePlage(heure));
    }

    private Validation enregistrerRefus(Billet billet, AgentControle agent, String motif) {
        Validation v = new Validation();
        v.setDateHeure(LocalDateTime.now());
        v.setResultat(ResultatValidation.REFUSEE);
        v.setMotifRefus(motif);
        v.setAgent(agent);
        v.setBillet(billet);
        try { return validationRepository.save(v); } catch (Exception e) { return v; }
    }

    public List<Validation> getHistorique(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }
}