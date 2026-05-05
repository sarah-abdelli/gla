package com.ticketeer.controller;

import com.ticketeer.entity.*;
import com.ticketeer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.List;
import java.util.Map;

// ============================================================
// AuthController — POST /api/auth/login
// ============================================================
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
class AuthController {

    @Autowired private AgentControleRepository agentRepository;
    @Autowired private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String mdp = body.get("motDePasse");

        // TODO : vérifier avec BCrypt en prod
        AgentControle agent = agentRepository.findByLogin(login)
                .orElse(null);

        if (agent == null || !agent.getMotDePasse().equals(mdp)) {
            return ResponseEntity.status(401).body(Map.of("erreur", "Identifiants invalides"));
        }

        Token token = tokenService.genererToken(agent);
        return ResponseEntity.ok(Map.of(
                "token", token.getValeur(),
                "agentNom", agent.getNom(),
                "expiration", token.getDateExpiration().toString()
        ));
    }
}

// ============================================================
// ItineraireController — GET /api/itineraires/search
// ============================================================
@RestController
@RequestMapping("/api/itineraires")
@CrossOrigin(origins = "*")
class ItineraireController {

    @Autowired private ItineraireService itineraireService;

    @GetMapping("/search")
    public ResponseEntity<List<Itineraire>> search(
            @RequestParam String depart,
            @RequestParam String arrivee) {
        List<Itineraire> resultats = itineraireService.rechercherItineraires(depart, arrivee);
        return ResponseEntity.ok(resultats);
    }
}

// ============================================================
// BilletController — /api/billets
// ============================================================
@RestController
@RequestMapping("/api/billets")
@CrossOrigin(origins = "*")
class BilletController {

    @Autowired private BilletService billetService;

    // Créer un billet
    @PostMapping("/create")
    public ResponseEntity<Billet> create(@RequestBody Map<String, Long> body) {
        Billet billet = billetService.creerBillet(
                body.get("voyageurId"),
                body.get("itineraireId")
        );
        return ResponseEntity.ok(billet);
    }

    // Récupérer un billet par UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Billet> getBillet(@PathVariable String uuid) {
        return ResponseEntity.ok(billetService.getBillet(uuid));
    }

    // Récupérer l'UUID pour générer le QR Code côté frontend
    @GetMapping("/{uuid}/qr")
    public ResponseEntity<Map<String, String>> getQR(@PathVariable String uuid) {
        String qrData = billetService.getQRData(uuid);
        return ResponseEntity.ok(Map.of("uuid", qrData));
    }
}

// ============================================================
// ValidationController — /api/validations
// ============================================================
@RestController
@RequestMapping("/api/validations")
@CrossOrigin(origins = "*")
class ValidationController {

    @Autowired private ValidationService validationService;

    // Valider un billet (appelé par l'app Android)
    @PostMapping("/validate")
    public ResponseEntity<Validation> validate(@RequestBody Map<String, String> body,
                                                @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String uuid = body.get("uuid");
        String numeroTrain = body.get("numeroTrain");
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime heure = LocalTime.parse(body.get("heure"));

        Validation validation = validationService.validerBillet(uuid, token, numeroTrain, date, heure);
        return ResponseEntity.ok(validation);
    }

    // Historique des validations d'un billet (pour l'admin)
    @GetMapping("/history/{uuid}")
    public ResponseEntity<List<Validation>> history(@PathVariable String uuid) {
        return ResponseEntity.ok(validationService.getHistorique(uuid));
    }
}

// ============================================================
// AdminController — /api/admin
// ============================================================
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
class AdminController {

    @Autowired private AdminService adminService;

    @PostMapping("/villes")
    public ResponseEntity<Ville> addVille(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.ajouterVille(body.get("nom")));
    }

    @GetMapping("/villes")
    public ResponseEntity<List<Ville>> getVilles() {
        return ResponseEntity.ok(adminService.getToutesLesVilles());
    }

    @PostMapping("/trains")
    public ResponseEntity<Train> addTrain(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.ajouterTrain(body.get("numero")));
    }

    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getTrains() {
        return ResponseEntity.ok(adminService.getTousLesTrains());
    }

    @PostMapping("/segments")
    public ResponseEntity<SegmentTrajet> addSegment(@RequestBody Map<String, String> body) {
        SegmentTrajet segment = adminService.ajouterSegment(
                body.get("villeDepart"),
                body.get("villeArrivee"),
                body.get("numeroTrain"),
                LocalDate.parse(body.get("date")),
                LocalTime.parse(body.get("heureDepart")),
                LocalTime.parse(body.get("heureArrivee"))
        );
        return ResponseEntity.ok(segment);
    }

    @GetMapping("/tracabilite/{uuid}")
    public ResponseEntity<List<Validation>> tracabilite(@PathVariable String uuid) {
        return ResponseEntity.ok(adminService.consulterTracabilite(uuid));
    }
}
