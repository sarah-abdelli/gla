package com.ticketeer.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ItineraireRequest {

    private List<SegmentRequest> segments;

    @Getter
    @Setter
    public static class SegmentRequest {

        private Long segmentId;
        private String villeDepart;
        private String villeArrivee;
        private String numeroTrain;
        private LocalDate date;
        private LocalTime heureDepart;
        private LocalTime heureArrivee;

        public boolean estExistant() {
            return segmentId != null;
        }
    }
}