package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.enums.ResultatValidation;
import com.ticketeer.repository.*;
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
        AgentControle agent = tokenService.getAgentParToken(tokenValeur);
        if (agent == null) {
            return enregistrerRefus(uuid, null, "Token invalide ou absent");
        }

        Billet billet = billetRepository.findByUuid(uuid).orElse(null);
        if (billet == null) {
            return enregistrerRefus(uuid, agent, "UUID inconnu");
        }

        if (!billet.estValide()) {
            return enregistrerRefus(uuid, agent, "Billet déjà utilisé ou invalide");
        }

        billet.marquerUtilise();
        billetRepository.save(billet);

        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());
        validation.setResultat(ResultatValidation.ACCEPTEE);
        validation.setBillet(billet);
        validation.setAgent(agent);
        return validationRepository.save(validation);
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
