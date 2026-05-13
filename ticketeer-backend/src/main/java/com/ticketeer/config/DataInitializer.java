package com.ticketeer.config;

import com.ticketeer.entity.*;
import com.ticketeer.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

            for (String nom : new String[]{
                    "Paris", "Lyon", "Marseille", "Bordeaux", "Lille",
                    "Strasbourg", "Nantes", "Toulouse", "Nice", "Montpellier"})
                villeRepo.save(new Ville(nom));

            Map<String, Ville> V = new HashMap<>();
            villeRepo.findAll().forEach(v -> V.put(v.getNom(), v));
            System.out.println("✅ 10 villes insérées");


            for (String n : new String[]{
                    "TGV001", "TGV002", "TGV003", "TGV004", "TGV005",
                    "IC100", "IC200", "IC300", "IC400", "IC500"})
                trainRepo.save(new Train(n));
            List<Train> trains = trainRepo.findAll();
            System.out.println("✅ 10 trains insérés");

            LocalDate today = LocalDate.now();
            List<Itineraire> itineraires = new ArrayList<>();

            // Directs Paris  Lyon
            String[][] directs = {
                    {"Paris", "Lyon",       "08:00", "10:00", "TGV001"},
                    {"Paris", "Lyon",       "12:00", "14:00", "TGV002"},
                    {"Lyon",  "Paris",      "09:00", "11:00", "TGV003"},
                    {"Lyon",  "Paris",      "15:00", "17:00", "TGV004"},
                    {"Paris", "Marseille",  "07:00", "10:15", "TGV005"},
                    {"Marseille", "Paris",  "08:00", "11:15", "IC100"},
                    {"Paris", "Lille",      "08:00", "09:00", "IC200"},
                    {"Lille", "Paris",      "10:00", "11:00", "IC300"},
                    {"Lyon",  "Marseille",  "09:00", "10:35", "IC400"},
                    {"Marseille", "Lyon",   "11:00", "12:35", "IC500"},
            };

            for (String[] d : directs) {
                Ville dep = V.get(d[0]);
                Ville arr = V.get(d[1]);
                Train train = trains.stream()
                        .filter(t -> t.getNumero().equals(d[4]))
                        .findFirst().orElse(trains.get(0));

                SegmentTrajet seg = new SegmentTrajet();
                seg.setVilleDepart(dep);
                seg.setVilleArrivee(arr);
                seg.setTrain(train);
                seg.setDateDepart(today);
                seg.setHeureDepart(LocalTime.parse(d[2]));
                seg.setHeureArrivee(LocalTime.parse(d[3]));
                seg.setPlacesDisponibles(200);

                Itineraire it = new Itineraire();
                it.getSegments().add(seg);
                itineraires.add(it);
            }

            // Correspondance Paris  Lyon → Marseille
            SegmentTrajet seg1 = new SegmentTrajet();
            seg1.setVilleDepart(V.get("Paris"));
            seg1.setVilleArrivee(V.get("Lyon"));
            seg1.setTrain(trains.get(0));
            seg1.setDateDepart(today);
            seg1.setHeureDepart(LocalTime.of(8, 0));
            seg1.setHeureArrivee(LocalTime.of(10, 0));
            seg1.setPlacesDisponibles(200);

            SegmentTrajet seg2 = new SegmentTrajet();
            seg2.setVilleDepart(V.get("Lyon"));
            seg2.setVilleArrivee(V.get("Marseille"));
            seg2.setTrain(trains.get(1));
            seg2.setDateDepart(today);
            seg2.setHeureDepart(LocalTime.of(10, 45));
            seg2.setHeureArrivee(LocalTime.of(12, 20));
            seg2.setPlacesDisponibles(200);

            Itineraire itCorr = new Itineraire();
            itCorr.getSegments().add(seg1);
            itCorr.getSegments().add(seg2);
            itineraires.add(itCorr);

            List<Itineraire> saved = itineraireRepo.saveAll(itineraires);
            System.out.println("✅ " + saved.size() + " itinéraires de test insérés");


            for (String[] v : new String[][]{
                    {"Alice Dupont",  "alice@test.com",  "pass123"},
                    {"Bob Martin",    "bob@test.com",    "pass123"},
                    {"Clara Petit",   "clara@test.com",  "pass123"},
                    {"David Leroy",   "david@test.com",  "pass123"}})
                voyageurRepo.save(new Voyageur(v[0], v[1], passwordEncoder.encode(v[2])));
            System.out.println("✅ 4 voyageurs insérés (BCrypt)");

            agentRepo.save(new AgentControle("Agent Dupuis",  "agent1", passwordEncoder.encode("agent123")));
            agentRepo.save(new AgentControle("Agent Bernard", "agent2", passwordEncoder.encode("agent456")));
            System.out.println("✅ 2 agents insérés (BCrypt)");

            Voyageur alice = voyageurRepo.findByEmail("alice@test.com").orElseThrow();
            Voyageur bob   = voyageurRepo.findByEmail("bob@test.com").orElseThrow();
            if (!saved.isEmpty()) billetRepo.save(Billet.creer(alice, saved.get(0)));
            if (saved.size() > 1) billetRepo.save(Billet.creer(bob,   saved.get(1)));
            System.out.println("✅ 2 billets de test insérés");

            System.out.println("🚀 BDD Ticketeer initialisée !");
            System.out.println("   Voyageur : alice@test.com / pass123");
            System.out.println("   Agent    : agent1 / agent123");
        };
    }
}