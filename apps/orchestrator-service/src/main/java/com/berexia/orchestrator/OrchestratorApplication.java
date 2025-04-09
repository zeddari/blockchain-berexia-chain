package com.berexia.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for the Orchestrator Service.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.berexia.orchestrator", "com.berexia.business.event.manager"})
@EntityScan(basePackages = {"com.berexia.orchestrator.models", "com.berexia.business.event.manager.models"})
@EnableJpaRepositories(basePackages = {"com.berexia.orchestrator.repositories", "com.berexia.business.event.manager.repositories"})
public class OrchestratorApplication {

    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }
} 