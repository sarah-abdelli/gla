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

        // 1. Vérifier le token de l'agent
        AgentControle agent = tokenService.getAgentParToken(tokenValeur);
        if (agent == null) {
            Validation v = new Validation();
            v.setDateHeure(LocalDateTime.now());
            v.setResultat(ResultatValidation.REFUSEE);
            v.setMotifRefus("Token invalide ou absent");
            return v; // retourne sans sauvegarder
        }

        // 2. Vérifier que le billet existe
        
        Billet billet = billetRepository.findById(uuid).orElse(null);
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

        // 4. Marquer le billet utilisé
        billet.marquerUtilise();
        billetRepository.save(billet);

        // 5. Enregistrer la validation ACCEPTEE
        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());
        validation.setResultat(ResultatValidation.ACCEPTEE);
        validation.setBillet(billet);
        validation.setAgent(agent);
        try {
            return validationRepository.save(validation);
        } catch (Exception e) {
            return validation;
        }
    }

    public List<Validation> getHistorique(String uuid) {
        return validationRepository.findByBilletUuid(uuid);
    }
}