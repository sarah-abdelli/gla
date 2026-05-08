package com.ticketeer.controller;

import com.ticketeer.entity.Billet;
import com.ticketeer.service.BilletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billets")
@CrossOrigin(origins = "*")
public class BilletController {

    @Autowired private BilletService billetService;

    @PostMapping("/create")
    public ResponseEntity<Billet> create(@RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(billetService.creerBillet(
                body.get("voyageurId"),
                body.get("itineraireId")
        ));
    }

    @GetMapping("/{uuid:.+}")
    public ResponseEntity<Billet> getBillet(@PathVariable String uuid) {
        return ResponseEntity.ok(billetService.getBillet(uuid));
    }

    @GetMapping("/{uuid:.+}/qr")
    public ResponseEntity<Map<String, String>> getQR(@PathVariable String uuid) {
        return ResponseEntity.ok(Map.of("uuid", billetService.getQRData(uuid)));
    }

    @GetMapping("/voyageur/{voyageurId}")
    public ResponseEntity<List<Billet>> getMesBillets(@PathVariable Long voyageurId) {
        return ResponseEntity.ok(billetService.getBilletsByVoyageur(voyageurId));
    }
}