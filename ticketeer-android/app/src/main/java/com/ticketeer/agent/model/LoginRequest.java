package com.ticketeer.agent.model;

// ---- Requête login ----
public class LoginRequest {
    private String login;
    private String motDePasse;

    public LoginRequest(String login, String motDePasse) {
        this.login = login;
        this.motDePasse = motDePasse;
    }

    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
}
