package com.ticketeer.controller;

import com.ticketeer.entity.Validation;
import com.ticketeer.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validations")
@CrossOrigin(origins = "*")
public class ValidationController {

    @Autowired private ValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validate(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String uuid = body.get("uuid");
        String numeroTrain = body.get("numeroTrain");
        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime heure = LocalTime.parse(body.get("heure"));

        Validation validation = validationService.validerBillet(uuid, token, numeroTrain, date, heure);

        // Retourner un Map simple au lieu de l'entité Validation
        Map<String, String> response = new HashMap<>();
        response.put("resultat", validation.getResultat() != null
                ? validation.getResultat().toString() : "REFUSEE");
        response.put("motifRefus", validation.getMotifRefus() != null
                ? validation.getMotifRefus() : "");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{uuid}")
    public ResponseEntity<List<Validation>> history(@PathVariable String uuid) {
        return ResponseEntity.ok(validationService.getHistorique(uuid));
    }
}