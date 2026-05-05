package com.ticketeer.controller;

import com.ticketeer.entity.Validation;
import com.ticketeer.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validations")
@CrossOrigin(origins = "*")
public class ValidationController {

    @Autowired private ValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<Validation> validate(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(validationService.validerBillet(
                body.get("uuid"),
                token,
                body.get("numeroTrain"),
                LocalDate.parse(body.get("date")),
                LocalTime.parse(body.get("heure"))
        ));
    }

    @GetMapping("/history/{uuid}")
    public ResponseEntity<List<Validation>> history(@PathVariable String uuid) {
        return ResponseEntity.ok(validationService.getHistorique(uuid));
    }
}
