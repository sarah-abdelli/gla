package com.ticketeer.agent.model;

public class ValidationResponse {
    private Long id;
    private String resultat; // "ACCEPTEE" ou "REFUSEE"
    private String motifRefus;
    private String dateHeure;

    public Long getId() { return id; }
    public String getResultat() { return resultat; }
    public String getMotifRefus() { return motifRefus; }
    public String getDateHeure() { return dateHeure; }

    public boolean isAcceptee() {
        return "ACCEPTEE".equals(resultat);
    }
}
