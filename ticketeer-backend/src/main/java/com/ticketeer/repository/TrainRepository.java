package com.ticketeer.repository;

import com.ticketeer.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByNumero(String numero);
    boolean existsByNumero(String numero);
}
