package com.ticketeer.controller;

import com.ticketeer.entity.AgentControle;
import com.ticketeer.entity.Token;
import com.ticketeer.entity.Voyageur;
import com.ticketeer.repository.AgentControleRepository;
import com.ticketeer.repository.VoyageurRepository;
import com.ticketeer.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AgentControleRepository agentRepository;
    @Autowired private VoyageurRepository voyageurRepository;
    @Autowired private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Endpoint pour l'app Android (agents uniquement)
    @PostMapping("/login")
    public ResponseEntity<?> loginAgent(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String mdp   = body.get("motDePasse");

        AgentControle agent = agentRepository.findByLogin(login).orElse(null);

        if (agent == null || !passwordEncoder.matches(mdp, agent.getMotDePasse())) {
            return ResponseEntity.status(401).body(Map.of("erreur", "Identifiants invalides"));
        }

        // Bloquer les comptes ADMIN sur l'app Android
        if ("ADMIN".equals(agent.getRole())) {
            return ResponseEntity.status(403).body(Map.of("erreur", "Accès refusé"));
        }

        Token token = tokenService.genererToken(agent);
        return ResponseEntity.ok(Map.of(
                "token", token.getValeur(),
                "agentNom", agent.getNom(),
                "expiration", token.getDateExpiration().toString()
        ));
    }

    // Endpoint pour l'app Admin (admins uniquement)
    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String mdp   = body.get("motDePasse");

        AgentControle agent = agentRepository.findByLogin(login).orElse(null);

        if (agent == null || !passwordEncoder.matches(mdp, agent.getMotDePasse())) {
            return ResponseEntity.status(401).body(Map.of("erreur", "Identifiants invalides"));
        }

        // Seuls les ADMIN peuvent accéder au panneau admin
        if (!"ADMIN".equals(agent.getRole())) {
            return ResponseEntity.status(403).body(Map.of("erreur", "Accès réservé aux administrateurs"));
        }

        return ResponseEntity.ok(Map.of(
                "nom", agent.getNom(),
                "login", agent.getLogin()
        ));
    }

    // Endpoint pour le site voyageur
    @PostMapping("/login/voyageur")
    public ResponseEntity<?> loginVoyageur(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String mdp   = body.get("motDePasse");

        Voyageur voyageur = voyageurRepository.findByEmail(email).orElse(null);

        if (voyageur == null || !passwordEncoder.matches(mdp, voyageur.getMotDePasse())) {
            return ResponseEntity.status(401).body(Map.of("erreur", "Email ou mot de passe incorrect"));
        }

        return ResponseEntity.ok(Map.of(
                "id", voyageur.getId(),
                "nom", voyageur.getNom(),
                "email", voyageur.getEmail()
        ));
    }
}