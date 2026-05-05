package com.ticketeer.repository;

import com.ticketeer.entity.AgentControle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AgentControleRepository extends JpaRepository<AgentControle, Long> {
    Optional<AgentControle> findByLogin(String login);
}
