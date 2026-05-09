package com.ticketeer.service;

import com.ticketeer.entity.AgentControle;
import com.ticketeer.entity.Token;
import com.ticketeer.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public Token genererToken(AgentControle agent) {
        Token token = new Token();
        token.setAgent(agent);
        token.setDateExpiration(LocalDateTime.now().plusHours(24));
        token.setValeur("JWT_" + agent.getLogin() + "_" + System.currentTimeMillis());
        return tokenRepository.save(token);
    }

    public boolean validerToken(String valeur) {
        return tokenRepository.findByValeur(valeur)
                .map(Token::estValide)
                .orElse(false);
    }

    public AgentControle getAgentParToken(String valeurToken) {
        return tokenRepository.findByValeur(valeurToken)
                .filter(Token::estValide)
                .map(Token::getAgent)
                .orElse(null);
    }

    public boolean validerTokenParValeur(String valeur) {
        return validerToken(valeur);
    }
}