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

    private static class Route {
        final String dep, arr;
        final int    dureeMins;
        final int[]  hDeparts;

        Route(String dep, String arr, int dureeMins, int... hDeparts) {
            this.dep = dep; this.arr = arr;
            this.dureeMins = dureeMins; this.hDeparts = hDeparts;
        }
    }

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

            // ── 1. VILLES ─────────────────────────────────────────────────────
            for (String nom : new String[]{
                    "Paris","Lyon","Marseille","Bordeaux","Lille",
                    "Strasbourg","Nantes","Toulouse","Nice","Montpellier"})
                villeRepo.save(new Ville(nom));

            Map<String, Ville> V = new HashMap<>();
            villeRepo.findAll().forEach(v -> V.put(v.getNom(), v));
            System.out.println("✅ 10 villes insérées");

            // ── 2. TRAINS ─────────────────────────────────────────────────────
            for (String n : new String[]{
                    "TGV001","TGV002","TGV003","TGV004","TGV005",
                    "IC100","IC200","IC300","IC400","IC500"})
                trainRepo.save(new Train(n));
            List<Train> trains = trainRepo.findAll();
            System.out.println("✅ 10 trains insérés");

            // ── 3. ROUTES DIRECTES ────────────────────────────────────────────
            List<Route> directes = Arrays.asList(
                    // Paris ↔ grandes villes
                    new Route("Paris","Lyon",          120, 630,800,1000,1200,1400,1600,1800,2000),
                    new Route("Lyon","Paris",           120, 630,800,1000,1200,1400,1600,1800,2000),
                    new Route("Paris","Marseille",      195, 700,930,1200,1500,1800),
                    new Route("Marseille","Paris",      195, 630,900,1130,1430,1730),
                    new Route("Paris","Bordeaux",       130, 730,1000,1300,1600,1900),
                    new Route("Bordeaux","Paris",       130, 700,930,1230,1530,1830),
                    new Route("Paris","Lille",           60, 600,700,800,1000,1200,1400,1600,1800,2000),
                    new Route("Lille","Paris",           60, 600,700,800,1000,1200,1400,1600,1800,2000),
                    new Route("Paris","Strasbourg",     107, 700,900,1100,1400,1700,1930),
                    new Route("Strasbourg","Paris",     107, 700,900,1100,1400,1700,1930),
                    new Route("Paris","Nantes",         120, 700,930,1200,1500,1800,2000),
                    new Route("Nantes","Paris",         120, 700,930,1200,1500,1800,2000),
                    new Route("Paris","Toulouse",       260, 700,1000,1400,1700),
                    new Route("Toulouse","Paris",       260, 700,1000,1400,1700),
                    // Lyon hub
                    new Route("Lyon","Marseille",        95, 700,900,1100,1300,1500,1700,1900),
                    new Route("Marseille","Lyon",        95, 700,900,1100,1300,1500,1700,1900),
                    new Route("Lyon","Montpellier",      75, 800,1000,1200,1400,1600,1800),
                    new Route("Montpellier","Lyon",      75, 800,1000,1200,1400,1600,1800),
                    new Route("Lyon","Strasbourg",      110, 730,930,1200,1500,1800),
                    new Route("Strasbourg","Lyon",      110, 730,930,1200,1500,1800),
                    new Route("Lyon","Nice",            240, 700,1100,1500,1800),
                    new Route("Nice","Lyon",            240, 700,1100,1500,1800),
                    new Route("Lyon","Toulouse",        150, 800,1200,1600),
                    new Route("Toulouse","Lyon",        150, 800,1200,1600),
                    // Marseille hub
                    new Route("Marseille","Nice",       150, 730,930,1130,1400,1630,1900),
                    new Route("Nice","Marseille",       150, 730,930,1130,1400,1630,1900),
                    new Route("Marseille","Montpellier",105, 700,900,1100,1300,1600,1900),
                    new Route("Montpellier","Marseille",105, 700,900,1100,1300,1600,1900),
                    new Route("Marseille","Toulouse",   150, 700,1000,1300,1630),
                    new Route("Toulouse","Marseille",   150, 700,1000,1300,1630),
                    // Sud
                    new Route("Toulouse","Montpellier",  60, 730,930,1130,1330,1530,1730,1930),
                    new Route("Montpellier","Toulouse",  60, 730,930,1130,1330,1530,1730,1930),
                    new Route("Nice","Montpellier",     180, 700,1000,1300,1600),
                    new Route("Montpellier","Nice",     180, 700,1000,1300,1600),
                    // Bordeaux hub
                    new Route("Bordeaux","Toulouse",    130, 700,900,1200,1500,1800),
                    new Route("Toulouse","Bordeaux",    130, 700,900,1200,1500,1800),
                    new Route("Bordeaux","Nantes",      120, 800,1100,1400,1700),
                    new Route("Nantes","Bordeaux",      120, 800,1100,1400,1700),
                    // Nord/Est
                    new Route("Lille","Strasbourg",     210, 700,1100,1500),
                    new Route("Strasbourg","Lille",     210, 700,1100,1500),
                    new Route("Lille","Nantes",         210, 730,1200,1630),
                    new Route("Nantes","Lille",         210, 730,1200,1630),
                    // Bordeaux ↔ Lyon (active Bordeaux→Nice, Bordeaux→Montpellier, Bordeaux→Strasbourg via Lyon)
                    new Route("Bordeaux","Lyon",        180, 700,1000,1400,1700),
                    new Route("Lyon","Bordeaux",        180, 700,1000,1400,1700)
            );

            Map<String, List<Route>> parDepart = new HashMap<>();
            for (Route r : directes)
                parDepart.computeIfAbsent(r.dep, k -> new ArrayList<>()).add(r);

            Set<String> hubs = Set.of("Paris", "Lyon", "Marseille", "Bordeaux");
            int[] placesPool = {200, 200, 180, 160, 150, 140, 120, 100, 80, 60, 50};

            // ── 4. GÉNÉRATION ─────────────────────────────────────────────────
            LocalDate debut = LocalDate.now();
            LocalDate fin   = LocalDate.of(2026, 8, 1);

            int totalItins  = 0;
            int trainCycle  = 0;
            int placesCycle = 0;

            Itineraire itinAlice = null, itinBob = null;

            for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {

                List<Itineraire> batch = new ArrayList<>();

                // A. DIRECTS
                for (Route r : directes) {
                    Ville dep   = V.get(r.dep);
                    Ville arr   = V.get(r.arr);
                    Train train = trains.get(trainCycle++ % trains.size());

                    for (int hhmm : r.hDeparts) {
                        LocalTime hDep = LocalTime.of(hhmm / 100, hhmm % 100);
                        LocalTime hArr = hDep.plusMinutes(r.dureeMins);
                        if (hArr.isAfter(LocalTime.of(23, 59))) continue;

                        int places = placesPool[placesCycle++ % placesPool.length];

                        SegmentTrajet seg = new SegmentTrajet();
                        seg.setVilleDepart(dep);    seg.setVilleArrivee(arr);
                        seg.setTrain(train);         seg.setDateDepart(date);
                        seg.setHeureDepart(hDep);    seg.setHeureArrivee(hArr);
                        seg.setPlacesDisponibles(places);

                        Itineraire it = new Itineraire();
                        it.getSegments().add(seg);
                        batch.add(it);
                    }
                }

                // B. CORRESPONDANCES via hubs
                for (Route r1 : directes) {
                    if (!hubs.contains(r1.arr)) continue;

                    for (Route r2 : parDepart.getOrDefault(r1.arr, List.of())) {
                        if (r1.dep.equals(r2.arr)) continue;
                        if (r1.dep.equals(r2.dep)) continue;

                        Ville dep1 = V.get(r1.dep);
                        Ville via  = V.get(r1.arr);
                        Ville arr2 = V.get(r2.arr);
                        Train tr1  = trains.get(trainCycle++ % trains.size());
                        Train tr2  = trains.get(trainCycle++ % trains.size());

                        for (int h1 : r1.hDeparts) {
                            LocalTime hDep1   = LocalTime.of(h1 / 100, h1 % 100);
                            LocalTime hArr1   = hDep1.plusMinutes(r1.dureeMins);
                            LocalTime minDep2 = hArr1.plusMinutes(30);

                            for (int h2 : r2.hDeparts) {
                                LocalTime hDep2 = LocalTime.of(h2 / 100, h2 % 100);
                                if (!hDep2.isBefore(minDep2)) {
                                    LocalTime hArr2 = hDep2.plusMinutes(r2.dureeMins);
                                    if (hArr2.isAfter(LocalTime.of(23, 59))) break;

                                    int places = placesPool[placesCycle++ % placesPool.length];

                                    SegmentTrajet seg1 = new SegmentTrajet();
                                    seg1.setVilleDepart(dep1); seg1.setVilleArrivee(via);
                                    seg1.setTrain(tr1);        seg1.setDateDepart(date);
                                    seg1.setHeureDepart(hDep1); seg1.setHeureArrivee(hArr1);
                                    seg1.setPlacesDisponibles(places);

                                    SegmentTrajet seg2 = new SegmentTrajet();
                                    seg2.setVilleDepart(via);  seg2.setVilleArrivee(arr2);
                                    seg2.setTrain(tr2);        seg2.setDateDepart(date);
                                    seg2.setHeureDepart(hDep2); seg2.setHeureArrivee(hArr2);
                                    seg2.setPlacesDisponibles(places);

                                    Itineraire it = new Itineraire();
                                    it.getSegments().add(seg1);
                                    it.getSegments().add(seg2);
                                    batch.add(it);
                                    break;
                                }
                            }
                        }
                    }
                }

                List<Itineraire> saved = itineraireRepo.saveAll(batch);
                totalItins += saved.size();

                if (itinAlice == null && !saved.isEmpty()) itinAlice = saved.get(0);
                if (itinBob   == null && saved.size() > 1) itinBob   = saved.get(1);

                if (date.getDayOfMonth() == 1 || date.equals(debut))
                    System.out.println("  📅 " + date + " — " + totalItins + " itinéraires insérés jusqu'ici");
            }

            System.out.println("✅ " + totalItins + " itinéraires insérés (directs + correspondances, " + debut + " → " + fin + ")");

            // ── 5. VOYAGEURS ──────────────────────────────────────────────────
            for (String[] v : new String[][]{
                    {"Alice Dupont","alice@test.com","pass123"},
                    {"Bob Martin","bob@test.com","pass123"},
                    {"Clara Petit","clara@test.com","pass123"},
                    {"David Leroy","david@test.com","pass123"}})
                voyageurRepo.save(new Voyageur(v[0], v[1], passwordEncoder.encode(v[2])));
            System.out.println("✅ 4 voyageurs insérés (BCrypt)");

            // ── 6. AGENTS ─────────────────────────────────────────────────────
            agentRepo.save(new AgentControle("Agent Dupuis",  "agent1", passwordEncoder.encode("agent123")));
            agentRepo.save(new AgentControle("Agent Bernard", "agent2", passwordEncoder.encode("agent456")));
            System.out.println("✅ 2 agents insérés (BCrypt)");

            // ── 7. BILLETS DE TEST ─────────────────────────────────────────────
            Voyageur alice = voyageurRepo.findByEmail("alice@test.com").orElseThrow();
            Voyageur bob   = voyageurRepo.findByEmail("bob@test.com").orElseThrow();
            if (itinAlice != null) billetRepo.save(Billet.creer(alice, itinAlice));
            if (itinBob   != null) billetRepo.save(Billet.creer(bob,   itinBob));
            System.out.println("✅ 2 billets de test insérés");

            System.out.println("🚀 BDD Ticketeer initialisée !");
            System.out.println("   Voyageur : alice@test.com / pass123");
            System.out.println("   Agent    : agent1 / agent123");
        };
    }
}