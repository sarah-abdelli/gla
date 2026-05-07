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
        Billet billet = billetRepository.findByUuid(uuid).orElse(null);
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
            return validationRepository.save(v);
        }

        // 4. Vérifier que le train appartient à l'itinéraire
        if (!verifierSegment(billet, numeroTrain)) {
            return enregistrerRefus(uuid, agent, "Hors itinéraire - mauvais train");
        }

        // 5. Vérifier date + heure
        if (!verifierContexte(billet, numeroTrain, date, heure)) {
            return enregistrerRefus(uuid, agent, "Mauvaise date ou heure");
        }

        // 6. Si c'est le dernier segment, marquer le billet utilisé
        if (verifierDernierSegment(billet, numeroTrain)) {
            billet.marquerUtilise();
            billetRepository.save(billet);
        }

        // 7. Enregistrer la validation acceptée
        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());
        validation.setResultat(ResultatValidation.ACCEPTEE);
        validation.setBillet(billet);
        validation.setAgent(agent);

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
    }

    public List<Validation> getHistorique(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }
}