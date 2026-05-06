package com.ticketeer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration générale — beans partagés.
 * Sarah Badsi — Phase 1 : Entités JPA + BDD + Config
 */
@Configuration
public class AppConfig {

    /**
     * Bean PasswordEncoder BCrypt (force 12).
     * Injecté dans DataInitializer et les services d'auth.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}