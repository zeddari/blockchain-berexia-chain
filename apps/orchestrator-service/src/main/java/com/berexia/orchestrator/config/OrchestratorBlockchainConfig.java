package com.berexia.orchestrator.config;

import com.berexia.business.event.manager.blockchain.BlockchainConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "berexia.event.manager.blockchain.enabled", havingValue = "true", matchIfMissing = false)
public class OrchestratorBlockchainConfig {

    @Value("${blockchain.ethereum.rpc.http}")
    private String rpcUrl;

    @Value("${blockchain.ethereum.contracts.ship-arrival-address}")
    private String shipArrivalContractAddress;

    @Value("${blockchain.ethereum.contracts.dap-address}")
    private String dapContractAddress;

    @Value("${blockchain.ethereum.credentials.private-key}")
    private String privateKey;

    @Bean
    public BlockchainConnector blockchainConnector() {
        BlockchainConnector connector = new BlockchainConnector();
        connector.init();
        return connector;
    }
} 