package com.ticketeer.service;

import com.ticketeer.entity.*;
import com.ticketeer.enums.*;
import com.ticketeer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.List;

// ============================================================
// BilletService — création et gestion des billets
// ============================================================
@Service
class BilletService {

    @Autowired private BilletRepository billetRepository;
    @Autowired private VoyageurRepository voyageurRepository;
    @Autowired private ItineraireRepository itineraireRepository;

    public Billet creerBillet(Long voyageurId, Long itineraireId) {
        Voyageur voyageur = voyageurRepository.findById(voyageurId)
                .orElseThrow(() -> new RuntimeException("Voyageur introuvable"));
        Itineraire itineraire = itineraireRepository.findById(itineraireId)
                .orElseThrow(() -> new RuntimeException("Itinéraire introuvable"));

        Billet billet = new Billet();
        billet.setUuid(Billet.genererUUID());
        billet.setDateCreation(LocalDateTime.now());
        billet.setEtat(EtatBillet.VALIDE);
        billet.setVoyageur(voyageur);
        billet.setItineraire(itineraire);

        return billetRepository.save(billet);
    }

    public Billet getBillet(String uuid) {
        return billetRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Billet introuvable : " + uuid));
    }

    public void invaliderBillet(String uuid) {
        Billet billet = getBillet(uuid);
        billet.invalider();
        billetRepository.save(billet);
    }

    // TODO : générer le QR Code (retourner simplement l'UUID, le frontend génère le QR)
    public String getQRData(String uuid) {
        getBillet(uuid); // vérifie que le billet existe
        return uuid;
    }
}

// ============================================================
// ItineraireService — recherche de trajets
// ============================================================
@Service
class ItineraireService {

    @Autowired private ItineraireRepository itineraireRepository;
    @Autowired private SegmentTrajetRepository segmentTrajetRepository;

    public List<Itineraire> rechercherItineraires(String depart, String arrivee) {
        // 1. Chercher les trajets directs
        List<Itineraire> directs = itineraireRepository.findDirects(depart, arrivee);
        if (!directs.isEmpty()) return directs;

        // 2. TODO : chercher les correspondances (S2 du scénario)
        // Logique : trouver segments depart→X, puis X→arrivee
        return rechercherCorrespondances(depart, arrivee);
    }

    private List<Itineraire> rechercherCorrespondances(String depart, String arrivee) {
        // TODO : implémenter la logique de correspondance
        return List.of();
    }

    public boolean verifierCompatibilite(Itineraire itineraire) {
        return itineraire.estCompatible();
    }
}

// ============================================================
// ValidationService — validation des billets par les agents
// ============================================================
@Service
class ValidationService {

    @Autowired private BilletRepository billetRepository;
    @Autowired private ValidationRepository validationRepository;
    @Autowired private TokenService tokenService;

    public Validation validerBillet(String uuid, String tokenValeur,
                                     String numeroTrain, LocalDate date,
                                     LocalTime heure) {
        Validation validation = new Validation();
        validation.setDateHeure(LocalDateTime.now());

        // 1. Vérifier le token de l'agent
        AgentControle agent = tokenService.getAgentParToken(tokenValeur);
        if (agent == null) {
            return enregistrerRefus(uuid, null, "Token invalide ou absent");
        }
        validation.setAgent(agent);

        // 2. Vérifier que le billet existe
        Billet billet = billetRepository.findByUuid(uuid).orElse(null);
        if (billet == null) {
            return enregistrerRefus(uuid, agent, "UUID inconnu");
        }
        validation.setBillet(billet);

        // 3. Vérifier l'état du billet
        if (!billet.estValide()) {
            return enregistrerRefus(uuid, agent, "Billet déjà utilisé ou invalide");
        }

        // 4. TODO : vérifier le segment (train, date, heure)
        // boolean segmentOk = verifierSegment(billet, numeroTrain, date, heure);

        // 5. Marquer le billet utilisé et enregistrer la validation
        billet.marquerUtilise();
        billetRepository.save(billet);

        validation.setResultat(ResultatValidation.ACCEPTEE);
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

// ============================================================
// AdminService — gestion du réseau ferroviaire
// ============================================================
@Service
class AdminService {

    @Autowired private VilleRepository villeRepository;
    @Autowired private TrainRepository trainRepository;
    @Autowired private SegmentTrajetRepository segmentRepository;
    @Autowired private ValidationRepository validationRepository;
    @Autowired private VoyageurRepository voyageurRepository;

    public Ville ajouterVille(String nom) {
        if (villeRepository.existsByNom(nom)) {
            throw new RuntimeException("Ville déjà existante : " + nom);
        }
        Ville ville = new Ville();
        ville.setNom(nom);
        return villeRepository.save(ville);
    }

    public Train ajouterTrain(String numero) {
        if (trainRepository.existsByNumero(numero)) {
            throw new RuntimeException("Train déjà existant : " + numero);
        }
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

    public void gererCompteVoyageur(Voyageur voyageur) {
        voyageurRepository.save(voyageur);
    }

    public List<Ville> getToutesLesVilles() {
        return villeRepository.findAll();
    }

    public List<Train> getTousLesTrains() {
        return trainRepository.findAll();
    }
}

// ============================================================
// TokenService — authentification des agents
// ============================================================
@Service
class TokenService {

    @Autowired private TokenRepository tokenRepository;
    @Autowired private AgentControleRepository agentRepository;

    public Token genererToken(AgentControle agent) {
        Token token = new Token();
        token.setAgent(agent);
        token.setDateExpiration(LocalDateTime.now().plusHours(24));
        // TODO : générer la vraie valeur JWT (voir JwtUtil)
        token.setValeur("JWT_PLACEHOLDER_" + agent.getLogin());
        return tokenRepository.save(token);
    }

    public AgentControle getAgentParToken(String valeurToken) {
        return tokenRepository.findByValeur(valeurToken)
                .filter(Token::estValide)
                .map(Token::getAgent)
                .orElse(null);
    }

    public boolean validerToken(String valeur) {
        return tokenRepository.findByValeur(valeur)
                .map(Token::estValide)
                .orElse(false);
    }
}
