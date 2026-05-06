package com.ticketeer.agent.model;

// ---- Requête de validation ----
public class ValidationRequest {
    private String uuid;
    private String numeroTrain;
    private String date;
    private String heure;

    public ValidationRequest(String uuid, String numeroTrain, String date, String heure) {
        this.uuid = uuid;
        this.numeroTrain = numeroTrain;
        this.date = date;
        this.heure = heure;
    }

    public String getUuid() { return uuid; }
    public String getNumeroTrain() { return numeroTrain; }
    public String getDate() { return date; }
    public String getHeure() { return heure; }
}
