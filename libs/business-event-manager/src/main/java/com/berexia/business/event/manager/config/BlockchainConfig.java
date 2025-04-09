package com.berexia.business.event.manager.config;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for blockchain integration.
 * This class is conditionally enabled based on the property berexia.event.manager.blockchain.enabled.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "berexia.event.manager.blockchain.enabled", havingValue = "true", matchIfMissing = false)
public class BlockchainConfig {

    private final BlockchainConnector blockchainConnector;
    
    /**
     * Initializes the blockchain connector after dependency injection.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing blockchain integration");
        blockchainConnector.init();
    }
} 