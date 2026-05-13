package com.ticketeer.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public class ItineraireRequest {

    private List<SegmentRequest> segments;

    public List<SegmentRequest> getSegments() { return segments; }
    public void setSegments(List<SegmentRequest> segments) { this.segments = segments; }

    public static class SegmentRequest {

        // Si existant  on utilise l'ID
        private Long segmentId;

        // Si nouveau  on utilise ces champs
        private String villeDepart;
        private String villeArrivee;
        private String numeroTrain;
        private LocalDate date;
        private LocalTime heureDepart;
        private LocalTime heureArrivee;

        public boolean estExistant() { return segmentId != null; }

        public Long getSegmentId() { return segmentId; }
        public void setSegmentId(Long segmentId) { this.segmentId = segmentId; }

        public String getVilleDepart() { return villeDepart; }
        public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

        public String getVilleArrivee() { return villeArrivee; }
        public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

        public String getNumeroTrain() { return numeroTrain; }
        public void setNumeroTrain(String numeroTrain) { this.numeroTrain = numeroTrain; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public LocalTime getHeureDepart() { return heureDepart; }
        public void setHeureDepart(LocalTime heureDepart) { this.heureDepart = heureDepart; }

        public LocalTime getHeureArrivee() { return heureArrivee; }
        public void setHeureArrivee(LocalTime heureArrivee) { this.heureArrivee = heureArrivee; }
    }
}
