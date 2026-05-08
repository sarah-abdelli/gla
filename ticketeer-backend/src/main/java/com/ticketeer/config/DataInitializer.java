package com.ticketeer.config;

import com.ticketeer.entity.*;
import com.ticketeer.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            VilleRepository villeRepo,
            TrainRepository trainRepo,
            SegmentTrajetRepository segmentRepo,
            ItineraireRepository itineraireRepo,
            VoyageurRepository voyageurRepo,
            AgentControleRepository agentRepo,
            BilletRepository billetRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (villeRepo.count() > 0) {
                System.out.println("ℹ️  BDD déjà initialisée — skip.");
                return;
            }

            // ── 1. VILLES ──────────────────────────────────────────────
            String[] nomsVilles = {
                    "Paris", "Lyon", "Marseille", "Bordeaux",
                    "Lille", "Strasbourg", "Nantes", "Toulouse",
                    "Nice", "Montpellier"
            };
            for (String nom : nomsVilles) villeRepo.save(new Ville(nom));
            System.out.println("✅ 10 villes insérées");

            Ville paris       = villeRepo.findByNom("Paris").orElseThrow();
            Ville lyon        = villeRepo.findByNom("Lyon").orElseThrow();
            Ville marseille   = villeRepo.findByNom("Marseille").orElseThrow();
            Ville bordeaux    = villeRepo.findByNom("Bordeaux").orElseThrow();
            Ville lille       = villeRepo.findByNom("Lille").orElseThrow();
            Ville strasbourg  = villeRepo.findByNom("Strasbourg").orElseThrow();
            Ville nantes      = villeRepo.findByNom("Nantes").orElseThrow();
            Ville toulouse    = villeRepo.findByNom("Toulouse").orElseThrow();
            Ville nice        = villeRepo.findByNom("Nice").orElseThrow();
            Ville montpellier = villeRepo.findByNom("Montpellier").orElseThrow();

            // ── 2. TRAINS ──────────────────────────────────────────────
            for (String num : new String[]{"TGV001", "TGV002", "TGV003", "IC100", "IC200"})
                trainRepo.save(new Train(num));
            System.out.println("✅ 5 trains insérés");

            Train tgv1 = trainRepo.findByNumero("TGV001").orElseThrow();
            Train tgv2 = trainRepo.findByNumero("TGV002").orElseThrow();
            Train tgv3 = trainRepo.findByNumero("TGV003").orElseThrow();
            Train ic1  = trainRepo.findByNumero("IC100").orElseThrow();
            Train ic2  = trainRepo.findByNumero("IC200").orElseThrow();

            // ── 3. SEGMENTS + ITINÉRAIRES ──────────────────────────────
            LocalDate today    = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // Itinéraire 1 : Paris → Lyon (direct)
            Itineraire itin1 = new Itineraire();
            itin1.getSegments().add(seg(paris, lyon, tgv1, today,
                    LocalTime.of(8, 0), LocalTime.of(10, 0)));
            itineraireRepo.save(itin1);

            // Itinéraire 2 : Paris → Marseille via Lyon (correspondance)
            Itineraire itin2 = new Itineraire();
            itin2.getSegments().add(seg(paris, lyon, tgv1, today,
                    LocalTime.of(8, 0), LocalTime.of(10, 0)));
            itin2.getSegments().add(seg(lyon, marseille, tgv2, today,
                    LocalTime.of(10, 30), LocalTime.of(12, 0)));
            itineraireRepo.save(itin2);

            // Itinéraire 3 : Paris → Bordeaux (direct)
            Itineraire itin3 = new Itineraire();
            itin3.getSegments().add(seg(paris, bordeaux, tgv3, today,
                    LocalTime.of(9, 0), LocalTime.of(11, 30)));
            itineraireRepo.save(itin3);

            // Itinéraire 4 : Lyon → Marseille (direct)
            Itineraire itin4 = new Itineraire();
            itin4.getSegments().add(seg(lyon, marseille, tgv2, today,
                    LocalTime.of(14, 0), LocalTime.of(15, 30)));
            itineraireRepo.save(itin4);

            // Itinéraire 5 : Lille → Strasbourg (direct, demain)
            Itineraire itin5 = new Itineraire();
            itin5.getSegments().add(seg(lille, strasbourg, ic1, tomorrow,
                    LocalTime.of(7, 30), LocalTime.of(10, 15)));
            itineraireRepo.save(itin5);

            // Itinéraire 6 : Nantes → Paris (direct)
            Itineraire itin6 = new Itineraire();
            itin6.getSegments().add(seg(nantes, paris, ic2, today,
                    LocalTime.of(6, 0), LocalTime.of(8, 30)));
            itineraireRepo.save(itin6);

            // Itinéraire 7 : Toulouse → Montpellier → Nice (correspondance)
            Itineraire itin7 = new Itineraire();
            itin7.getSegments().add(seg(toulouse, montpellier, tgv3, tomorrow,
                    LocalTime.of(9, 0), LocalTime.of(10, 30)));
            itin7.getSegments().add(seg(montpellier, nice, tgv2, tomorrow,
                    LocalTime.of(11, 0), LocalTime.of(12, 15)));
            itineraireRepo.save(itin7);

            System.out.println("✅ 7 itinéraires insérés");

            // ── 4. VOYAGEURS (BCrypt) ──────────────────────────────────
            String[][] vData = {
                    {"Alice Dupont", "alice@test.com", "pass123"},
                    {"Bob Martin",   "bob@test.com",   "pass123"},
                    {"Clara Petit",  "clara@test.com",  "pass123"},
                    {"David Leroy",  "david@test.com",  "pass123"}
            };
            for (String[] v : vData)
                voyageurRepo.save(new Voyageur(v[0], v[1], passwordEncoder.encode(v[2])));
            System.out.println("✅ 4 voyageurs insérés (BCrypt)");

            // ── 5. AGENTS (BCrypt) ─────────────────────────────────────
            agentRepo.save(new AgentControle("Agent Dupuis",  "agent1", passwordEncoder.encode("agent123")));
            agentRepo.save(new AgentControle("Agent Bernard", "agent2", passwordEncoder.encode("agent456")));
            System.out.println("✅ 2 agents insérés (BCrypt)");

            // ── 6. BILLETS DE TEST ─────────────────────────────────────
            Voyageur alice = voyageurRepo.findByEmail("alice@test.com").orElseThrow();
            Voyageur bob   = voyageurRepo.findByEmail("bob@test.com").orElseThrow();
            billetRepo.save(Billet.creer(alice, itin1));
            billetRepo.save(Billet.creer(bob, itin2));
            System.out.println("✅ 2 billets de test insérés");

            System.out.println("🚀 BDD Ticketeer initialisée !");
            System.out.println("   Voyageur : alice@test.com / pass123");
            System.out.println("   Agent    : agent1 / agent123");
        };
    }

    private SegmentTrajet seg(Ville dep, Ville arr, Train train,
                              LocalDate date, LocalTime hDep, LocalTime hArr) {
        SegmentTrajet s = new SegmentTrajet();
        s.setVilleDepart(dep);
        s.setVilleArrivee(arr);
        s.setTrain(train);
        s.setDateDepart(date);
        s.setHeureDepart(hDep);
        s.setHeureArrivee(hArr);
        return s;
    }
}