package com.berexia.business.event.manager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import com.berexia.business.event.manager.dto.DAPEvent;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;
import com.berexia.business.event.manager.service.DapRequestProcessor;
import com.berexia.business.event.manager.service.EventProcessor;
import com.berexia.business.event.manager.service.ShipArrivalProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for service beans such as processors.
 * Processors will only be created if the corresponding consumer is enabled via configuration properties.
 */
@Slf4j
@Configuration
public class ServiceConfig {

    /**
     * Creates a processor for ship arrival events
     *
     * @param blockchainConnector the blockchain connector
     * @return the processor
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.ship-arrival.enabled", havingValue = "true", matchIfMissing = true)
    public EventProcessor<ShipArrivalEvent> shipArrivalProcessor(BlockchainConnector blockchainConnector) {
        log.info("Creating ShipArrivalProcessor bean");
        return new ShipArrivalProcessor(blockchainConnector);
    }

    /**
     * Creates a processor for DAP events
     *
     * @param blockchainConnector the blockchain connector
     * @return the processor
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "berexia.event.manager.consumers.dap-request.enabled", havingValue = "true", matchIfMissing = true)
    public EventProcessor<DAPEvent> dapProcessor(BlockchainConnector blockchainConnector) {
        log.info("Creating DapRequestProcessor bean");
        return new DapRequestProcessor(blockchainConnector);
    }
} 