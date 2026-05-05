package com.ticketeer.config;

import com.ticketeer.entity.*;
import com.ticketeer.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.*;

/**
 * Initialise la base de données avec des données de test au démarrage.
 * 10 villes, quelques trains, segments et voyageurs de test.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            VilleRepository villeRepo,
            TrainRepository trainRepo,
            SegmentTrajetRepository segmentRepo,
            VoyageurRepository voyageurRepo,
            AgentControleRepository agentRepo) {

        return args -> {
            // Ne pas réinitialiser si déjà peuplé
            if (villeRepo.count() > 0) return;

            // ---- 10 Villes ----
            String[] nomsVilles = {"Paris", "Lyon", "Marseille", "Bordeaux",
                                    "Lille", "Strasbourg", "Nantes", "Toulouse",
                                    "Nice", "Montpellier"};
            for (String nom : nomsVilles) {
                Ville v = new Ville();
                v.setNom(nom);
                villeRepo.save(v);
            }

            // ---- Trains ----
            String[] numerosTrains = {"TGV001", "TGV002", "TGV003", "IC100", "IC200"};
            for (String num : numerosTrains) {
                Train t = new Train();
                t.setNumero(num);
                trainRepo.save(t);
            }

            // ---- Segments (quelques trajets de test) ----
            Ville paris = villeRepo.findByNom("Paris").orElseThrow();
            Ville lyon = villeRepo.findByNom("Lyon").orElseThrow();
            Ville marseille = villeRepo.findByNom("Marseille").orElseThrow();
            Ville bordeaux = villeRepo.findByNom("Bordeaux").orElseThrow();

            Train tgv1 = trainRepo.findByNumero("TGV001").orElseThrow();
            Train tgv2 = trainRepo.findByNumero("TGV002").orElseThrow();
            Train tgv3 = trainRepo.findByNumero("TGV003").orElseThrow();

            LocalDate today = LocalDate.now();

            // Paris → Lyon (direct)
            SegmentTrajet s1 = new SegmentTrajet();
            s1.setVilleDepart(paris); s1.setVilleArrivee(lyon);
            s1.setTrain(tgv1); s1.setDateDepart(today);
            s1.setHeureDepart(LocalTime.of(8, 0));
            s1.setHeureArrivee(LocalTime.of(10, 0));
            segmentRepo.save(s1);

            // Lyon → Marseille
            SegmentTrajet s2 = new SegmentTrajet();
            s2.setVilleDepart(lyon); s2.setVilleArrivee(marseille);
            s2.setTrain(tgv2); s2.setDateDepart(today);
            s2.setHeureDepart(LocalTime.of(10, 30));
            s2.setHeureArrivee(LocalTime.of(12, 0));
            segmentRepo.save(s2);

            // Paris → Bordeaux (direct)
            SegmentTrajet s3 = new SegmentTrajet();
            s3.setVilleDepart(paris); s3.setVilleArrivee(bordeaux);
            s3.setTrain(tgv3); s3.setDateDepart(today);
            s3.setHeureDepart(LocalTime.of(9, 0));
            s3.setHeureArrivee(LocalTime.of(11, 30));
            segmentRepo.save(s3);

            // ---- 4 Voyageurs de test ----
            String[][] voyageurs = {
                {"Alice Dupont", "alice@test.com", "pass123"},
                {"Bob Martin", "bob@test.com", "pass123"},
                {"Clara Petit", "clara@test.com", "pass123"},
                {"David Leroy", "david@test.com", "pass123"}
            };
            for (String[] v : voyageurs) {
                Voyageur voyageur = new Voyageur();
                voyageur.setNom(v[0]);
                voyageur.setEmail(v[1]);
                voyageur.setMotDePasse(v[2]); // TODO : BCrypt en prod
                voyageurRepo.save(voyageur);
            }

            // ---- 1 Agent de contrôle de test ----
            AgentControle agent = new AgentControle();
            agent.setNom("Agent Test");
            agent.setLogin("agent1");
            agent.setMotDePasse("agent123"); // TODO : BCrypt en prod
            agentRepo.save(agent);

            System.out.println("✅ Données de test initialisées !");
        };
    }
}
