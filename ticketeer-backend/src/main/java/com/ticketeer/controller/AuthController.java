package com.ticketeer.controller;

import com.ticketeer.entity.AgentControle;
import com.ticketeer.entity.Token;
import com.ticketeer.repository.AgentControleRepository;
import com.ticketeer.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AgentControleRepository agentRepository;
    @Autowired private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String mdp = body.get("motDePasse");

        AgentControle agent = agentRepository.findByLogin(login).orElse(null);
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
