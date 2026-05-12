package com.ticketeer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketeerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketeerApplication.class, args);
    }
}