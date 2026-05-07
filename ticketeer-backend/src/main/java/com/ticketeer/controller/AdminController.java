package com.ticketeer.controller;

import com.ticketeer.entity.*;
import com.ticketeer.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/villes")
    public ResponseEntity<Ville> addVille(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                adminService.ajouterVille(body.get("nom"))
        );
    }

    @GetMapping("/villes")
    public ResponseEntity<List<Ville>> getVilles() {
        return ResponseEntity.ok(
                adminService.getToutesLesVilles()
        );
    }

    @PostMapping("/trains")
    public ResponseEntity<Train> addTrain(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                adminService.ajouterTrain(body.get("numero"))
        );
    }

    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getTrains() {
        return ResponseEntity.ok(
                adminService.getTousLesTrains()
        );
    }

    @PostMapping("/segments")
    public ResponseEntity<SegmentTrajet> addSegment(@RequestBody Map<String, String> body) {

        return ResponseEntity.ok(
                adminService.ajouterSegment(
                        body.get("villeDepart"),
                        body.get("villeArrivee"),
                        body.get("numeroTrain"),
                        LocalDate.parse(body.get("date")),
                        LocalTime.parse(body.get("heureDepart")),
                        LocalTime.parse(body.get("heureArrivee"))
                )
        );
    }

    @GetMapping("/tracabilite/{uuid}")
    public ResponseEntity<List<Validation>> tracabilite(@PathVariable String uuid) {
        return ResponseEntity.ok(
                adminService.consulterTracabilite(uuid)
        );
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> getVoyageurs() {

        List<Map<String, Object>> voyageurs = adminService.getTousLesVoyageurs()
                .stream()
                .map(v -> {
                    Map<String, Object> m = new java.util.HashMap<>();

                    m.put("id", v.getId());
                    m.put("nom", v.getNom());
                    m.put("email", v.getEmail());

                    return m;
                })
                .toList();

        return ResponseEntity.ok(voyageurs);
    }

    @GetMapping("/validations")
    public ResponseEntity<List<Validation>> getValidations() {
        return ResponseEntity.ok(
                adminService.getToutesLesValidations()
        );
    }
    @DeleteMapping("/villes/{id}")
    public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
    adminService.supprimerVille(id);
    return ResponseEntity.noContent().build();
    }
}