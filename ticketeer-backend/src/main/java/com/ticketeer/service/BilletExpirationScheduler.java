package com.ticketeer.service;

import com.ticketeer.entity.Billet;
import com.ticketeer.entity.SegmentTrajet;
import com.ticketeer.enums.EtatBillet;
import com.ticketeer.repository.BilletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class BilletExpirationScheduler {

    @Autowired
    private BilletRepository billetRepository;

    // Tourne toutes les minutes
    @Scheduled(fixedRate = 60000)
    public void marquerBilletsExpires() {
        LocalDate aujourd8 = LocalDate.now();
        LocalTime maintenant = LocalTime.now();

        List<Billet> billetsValides = billetRepository.findByEtat(EtatBillet.VALIDE);

        List<Billet> aMarquer = billetsValides.stream()
                .filter(b -> estTotalementExpire(b, aujourd8, maintenant))
                .toList();

        for (Billet b : aMarquer) {
            b.marquerUtilise();
        }

        if (!aMarquer.isEmpty()) {
            billetRepository.saveAll(aMarquer);
            System.out.println("[Scheduler] " + aMarquer.size() + " billet(s) marqué(s) UTILISE (date passée)");
        }
    }

    // Un billet est expiré si TOUS ses segments sont dans le passé
    private boolean estTotalementExpire(Billet billet, LocalDate aujourd8, LocalTime maintenant) {
        List<SegmentTrajet> segments = billet.getItineraire().getSegments();
        if (segments == null || segments.isEmpty()) return false;

        for (SegmentTrajet s : segments) {
            // Si au moins un segment est encore dans le futur ou en cours → pas expiré
            if (s.getDateDepart().isAfter(aujourd8)) return false;
            if (s.getDateDepart().equals(aujourd8) && s.getHeureArrivee().isAfter(maintenant)) return false;
        }
        return true;
    }
}