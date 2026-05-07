package com.ticketeer.controller;

import com.ticketeer.entity.Itineraire;
import com.ticketeer.service.ItineraireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/itineraires")
@CrossOrigin(origins = "*")
public class ItineraireController {

    @Autowired private ItineraireService itineraireService;

    @GetMapping("/search")
    public ResponseEntity<List<Itineraire>> search(
            @RequestParam String depart,
            @RequestParam String arrivee) {
        return ResponseEntity.ok(itineraireService.rechercherItineraires(depart, arrivee));
    }
}
