package com.berexia.orchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Configuration
public class Web3jConfig {

    @Value("${web3j.client-url}")
    private String clientUrl;

    @Value("${blockchain.ethereum.credentials.private-key}")
    private String privateKey;

    @Value("${blockchain.ethereum.gas-price:20000000000}")
    private BigInteger gasPrice;

    @Value("${blockchain.ethereum.gas-limit:6721975}")
    private BigInteger gasLimit;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(clientUrl));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }

    @Bean
    public ContractGasProvider gasProvider() {
        return new StaticGasProvider(gasPrice, gasLimit);
    }
} 