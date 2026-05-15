package com.ticketeer.agent.model;

public class LoginResponse {
    private String token;
    private String agentNom;
    private String expiration;

    public String getToken() { return token; }
    public String getAgentNom() { return agentNom; }
    public String getExpiration() { return expiration; }
}
