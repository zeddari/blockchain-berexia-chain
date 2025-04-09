package com.berexia.business.event.manager.blockchain;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import com.berexia.business.event.manager.blockchain.contracts.DAPContract;
import com.berexia.business.event.manager.blockchain.contracts.ShipArrivalContract;
import com.berexia.business.event.manager.dto.DAPEvent;
import com.berexia.business.event.manager.dto.ShipArrivalEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for connecting to and interacting with blockchain smart contracts.
 * This service is responsible for recording events on the blockchain.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "berexia.event.manager.blockchain.enabled", havingValue = "true", matchIfMissing = false)
public class BlockchainConnector {

    private static final String DEFAULT_RPC_URL = "http://157.173.119.195:8545";
    
    // Default private key for development (DO NOT USE IN PRODUCTION)
    private static final String DEFAULT_PRIVATE_KEY = "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63";
    
    @Value("${berexia.event.manager.blockchain.rpc-url:" + DEFAULT_RPC_URL + "}")
    private String rpcUrl;
    
    @Value("${berexia.event.manager.blockchain.ship-arrival-contract:0x2F632b08EcE7E9DcA0fCff1F91c1D5Bc245440Eb}")
    private String shipArrivalContractAddress;
    
    @Value("${berexia.event.manager.blockchain.dap-contract:0x91227A9377f70670237Ed667Ad5D9017D1Bae688}")
    private String dapContractAddress;
    
    @Value("${berexia.event.manager.blockchain.private-key:" + DEFAULT_PRIVATE_KEY + "}")
    private String privateKey;
    
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider gasProvider;
    
    /**
     * Initializes the blockchain connector.
     * This method is called by Spring after dependency injection.
     */
    public void init() {
        try {
            // Validate configuration
            if (rpcUrl == null || rpcUrl.trim().isEmpty()) {
                rpcUrl = DEFAULT_RPC_URL;
                log.warn("RPC URL not provided, using default: {}", DEFAULT_RPC_URL);
            }
            
            if (privateKey == null || privateKey.trim().isEmpty()) {
                privateKey = DEFAULT_PRIVATE_KEY;
                log.warn("Private key not provided, using default development key. DO NOT USE IN PRODUCTION!");
            }
            
            if (shipArrivalContractAddress == null || shipArrivalContractAddress.trim().isEmpty()) {
                shipArrivalContractAddress = "0xC374D8cf8E99F688D9b66730644e664B5324A82f";
                //throw new IllegalStateException("Ship arrival contract address is required. Please set berexia.event.manager.blockchain.ship-arrival-contract in your configuration");
            }
            
            if (dapContractAddress == null || dapContractAddress.trim().isEmpty()) {
                dapContractAddress = "0x73851bBa7760E41D53203Fcd09DE256482cD5247";
                //throw new IllegalStateException("DAP contract address is required. Please set berexia.event.manager.blockchain.dap-contract in your configuration");
            }
            
            log.info("Initializing Blockchain Connector with RPC URL: {}", rpcUrl);
            
            // Initialize Web3j
            web3j = Web3j.build(new HttpService(rpcUrl));
            
            // Get client version and current block number to verify connection
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().send();
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            
            log.info("Connected to Ethereum client: {} at {}", clientVersion.getWeb3ClientVersion(), rpcUrl);
            log.info("Current block number: {}", blockNumber.getBlockNumber());
            
            // Initialize credentials
            credentials = Credentials.create(privateKey);
            log.info("Using account address: 0x{}", credentials.getAddress());
            
            // Initialize gas provider
            gasProvider = new DefaultGasProvider() {
                @Override
                public BigInteger getGasPrice() {
                    return BigInteger.ZERO;
                }
            };
            
            // Verify contract addresses
            log.info("Ship Arrival Contract: {}", shipArrivalContractAddress);
            log.info("DAP Contract: {}", dapContractAddress);
            
            log.info("Blockchain connector initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize blockchain connector: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize blockchain connector: " + e.getMessage(), e);
        }
    }
    
    /**
     * Records a ship arrival event on the blockchain.
     * This method is called asynchronously to avoid blocking the event processing.
     *
     * @param event The ship arrival event to record
     * @return A CompletableFuture that will be completed when the transaction is mined
     */
    public CompletableFuture<String> recordShipArrival(ShipArrivalEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Recording ship arrival event on blockchain: {}", event);
                
                // Validate required fields
                if (event.getNumeroAvis() == null) {
                    throw new IllegalArgumentException("numeroAvis is required");
                }
                
                // Load the contract
                ShipArrivalContract contract = ShipArrivalContract.load(
                    shipArrivalContractAddress,
                    web3j,
                    credentials,
                    gasProvider
                );
                
                // Call the smart contract method
                String transactionHash = contract.recordShipArrival(
                    BigInteger.valueOf(event.getNumeroAvis()),
                    event.getNumeroEscale(),
                    event.getNomPort(),
                    event.getCodePort(),
                    event.getNomNavire(),
                    event.getEtat()
                ).send().getTransactionHash();
                
                log.info("Ship arrival recorded on blockchain with transaction hash: {}", transactionHash);
                return transactionHash;
            } catch (Exception e) {
                log.error("Failed to record ship arrival on blockchain: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to record ship arrival on blockchain", e);
            }
        });
    }
    
    /**
     * Records a DAP event on the blockchain.
     * This method is called asynchronously to avoid blocking the event processing.
     *
     * @param event The DAP event to record
     * @return A CompletableFuture that will be completed when the transaction is mined
     */
    public CompletableFuture<String> recordDAP(DAPEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Recording DAP event on blockchain: {}", event);
                
                // Load the contract
                DAPContract contract = DAPContract.load(
                    dapContractAddress,
                    web3j,
                    credentials,
                    gasProvider
                );
                
                // Convert LocalDateTime to Unix timestamp
                BigInteger requestDate = event.getRequestDate() != null
                    ? BigInteger.valueOf(event.getRequestDate().toEpochSecond(ZoneOffset.UTC))
                    : BigInteger.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                
                // Call the smart contract method
                String transactionHash = contract.recordDAP(
                    BigInteger.valueOf(event.getNoticeNumber()),
                    event.getCallNumber(),
                    event.getPortName(),
                    event.getPortCode(),
                    event.getShipName(),
                    event.getOperatorName(),
                    event.getStatus(),
                    requestDate
                ).send().getTransactionHash();
                
                log.info("DAP recorded on blockchain with transaction hash: {}", transactionHash);
                return transactionHash;
            } catch (Exception e) {
                log.error("Failed to record DAP on blockchain: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to record DAP on blockchain", e);
            }
        });
    }
    
    /**
     * Updates a ship arrival event on the blockchain.
     *
     * @param event The ship arrival event to update
     * @return A CompletableFuture that will be completed when the transaction is mined
     */
    public CompletableFuture<String> updateShipArrival(ShipArrivalEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Updating ship arrival event on blockchain: {}", event);
                
                // Validate required fields
                if (event.getNumeroAvis() == null) {
                    throw new IllegalArgumentException("numeroAvis is required");
                }
                
                // Load the contract
                ShipArrivalContract contract = ShipArrivalContract.load(
                    shipArrivalContractAddress,
                    web3j,
                    credentials,
                    gasProvider
                );
                
                // Call the smart contract method
                String transactionHash = contract.updateShipArrival(
                    BigInteger.valueOf(event.getNumeroAvis()),
                    event.getNumeroEscale(),
                    event.getNomPort(),
                    event.getCodePort(),
                    event.getNomNavire(),
                    event.getEtat()
                ).send().getTransactionHash();
                
                log.info("Ship arrival updated on blockchain with transaction hash: {}", transactionHash);
                return transactionHash;
            } catch (Exception e) {
                log.error("Failed to update ship arrival on blockchain: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to update ship arrival on blockchain", e);
            }
        });
    }
    
    /**
     * Updates the status of a DAP on the blockchain.
     *
     * @param noticeNumber The notice number of the DAP to update
     * @param status The new status
     * @return A CompletableFuture that will be completed when the transaction is mined
     */
    public CompletableFuture<String> updateDAPStatus(Long noticeNumber, String status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Updating DAP status on blockchain for notice number: {}", noticeNumber);
                
                // Load the contract
                DAPContract contract = DAPContract.load(
                    dapContractAddress,
                    web3j,
                    credentials,
                    gasProvider
                );
                
                // Call the smart contract method
                String transactionHash = contract.updateDAPStatus(
                    BigInteger.valueOf(noticeNumber),
                    status
                ).send().getTransactionHash();
                
                log.info("DAP status updated on blockchain with transaction hash: {}", transactionHash);
                return transactionHash;
            } catch (Exception e) {
                log.error("Failed to update DAP status on blockchain: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to update DAP status on blockchain", e);
            }
        });
    }
    
    /**
     * Checks if a ship arrival event exists.
     *
     * @param numeroAvis The numeroAvis to check
     * @return true if the ship arrival exists, false otherwise
     */
    public boolean shipArrivalExists(Long numeroAvis) {
        try {
            if (numeroAvis == null) {
                throw new IllegalArgumentException("numeroAvis is required");
            }
            
            ShipArrivalContract contract = ShipArrivalContract.load(
                shipArrivalContractAddress,
                web3j,
                credentials,
                gasProvider
            );
            
            return contract.shipArrivalExists(BigInteger.valueOf(numeroAvis)).send();
        } catch (Exception e) {
            log.error("Failed to check if ship arrival exists: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if ship arrival exists", e);
        }
    }
    
    /**
     * Checks if a DAP exists.
     *
     * @param noticeNumber The notice number to check
     * @return true if the DAP exists, false otherwise
     */
    public boolean dapExists(Long noticeNumber) {
        try {
            if (noticeNumber == null) {
                throw new IllegalArgumentException("noticeNumber is required");
            }
            
            DAPContract contract = DAPContract.load(
                dapContractAddress,
                web3j,
                credentials,
                gasProvider
            );
            
            return contract.dapExists(BigInteger.valueOf(noticeNumber)).send();
        } catch (Exception e) {
            log.error("Failed to check if DAP exists: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if DAP exists", e);
        }
    }
} 