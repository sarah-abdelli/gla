package com.ticketeer.controller;

import com.ticketeer.entity.*;
import com.ticketeer.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

    @DeleteMapping("/villes/{id}")
    public ResponseEntity<Void> deleteVille(@PathVariable Long id) {
        adminService.supprimerVille(id);
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        adminService.supprimerTrain(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/segments")
    public ResponseEntity<List<SegmentTrajet>> getSegments() {
        return ResponseEntity.ok(
                adminService.getTousLesSegments()
        );
    }

    @DeleteMapping("/segments/{id}")
    public ResponseEntity<Void> deleteSegment(@PathVariable Long id) {
        adminService.supprimerSegment(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/clients")
    public ResponseEntity<List<Map<String, Object>>> getVoyageurs() {
        List<Map<String, Object>> voyageurs = adminService.getTousLesVoyageurs()
                .stream()
                .map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", v.getId());
                    m.put("nom", v.getNom());
                    m.put("email", v.getEmail());
                    m.put("billets", v.nombreBilletsValides());
                    return m;
                })
                .toList();
        return ResponseEntity.ok(voyageurs);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<Map<String, Object>> updateVoyageur(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Voyageur v = adminService.modifierVoyageur(id, body.get("nom"), body.get("email"));
        Map<String, Object> m = new HashMap<>();
        m.put("id", v.getId());
        m.put("nom", v.getNom());
        m.put("email", v.getEmail());
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteVoyageur(@PathVariable Long id) {
        adminService.supprimerVoyageur(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tracabilite/{uuid}")
    public ResponseEntity<List<Validation>> tracabilite(@PathVariable String uuid) {
        return ResponseEntity.ok(
                adminService.consulterTracabilite(uuid)
        );
    }

    @GetMapping("/validations")
    public ResponseEntity<List<Validation>> getValidations() {
        return ResponseEntity.ok(
                adminService.getToutesLesValidations()
        );
    }
}