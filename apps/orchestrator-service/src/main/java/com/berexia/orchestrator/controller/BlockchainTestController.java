package com.berexia.orchestrator.controller;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import com.berexia.business.event.manager.blockchain.contracts.DAPContract;
import com.berexia.business.event.manager.blockchain.contracts.ShipArrivalContract;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/blockchain/test")
@RequiredArgsConstructor
@Slf4j
public class BlockchainTestController {

    private final Web3j web3j;

    @Value("${blockchain.ethereum.contracts.ship-arrival-address}")
    private String shipArrivalContractAddress;

    @Value("${blockchain.ethereum.contracts.dap-address}")
    private String dapContractAddress;

    @Value("${blockchain.ethereum.credentials.private-key}")
    private String privateKey;

    @Data
    public static class ShipArrivalRequest {
        private Long numeroAvis;
        private String numeroEscale;
        private String nomPort;
        private String codePort;
        private String nomNavire;
        private LocalDateTime dateArrivee;
        private String etat;
    }

    @Data
    public static class DapRequest {
        private Long noticeNumber;
        private String callNumber;
        private String portName;
        private String portCode;
        private String shipName;
        private String operatorName;
        private String status;
        private Long requestDate;
    }

    @PostMapping("/ship-arrival")
    public ResponseEntity<Map<String, Object>> testShipArrivalContract(@RequestBody ShipArrivalRequest request) {
        try {
            // Create credentials from private key
            Credentials credentials = Credentials.create(privateKey);

            // Load the contract
            ShipArrivalContract contract = ShipArrivalContract.load(
                shipArrivalContractAddress,
                web3j,
                credentials,
                new DefaultGasProvider() {
                    @Override
                    public BigInteger getGasPrice() {
                        return BigInteger.ZERO;
                    }
                }
            );

            // Convert LocalDateTime to BigInteger (Unix timestamp)
            BigInteger arrivalTime = request.getDateArrivee() != null ? 
                BigInteger.valueOf(request.getDateArrivee().atZone(ZoneId.systemDefault()).toEpochSecond()) : 
                BigInteger.valueOf(Instant.now().getEpochSecond());

            // Call the contract function
            TransactionReceipt receipt = contract.recordShipArrival(
                BigInteger.valueOf(request.getNumeroAvis()),
                request.getNumeroEscale(),
                request.getNomPort(),
                request.getCodePort(),
                request.getNomNavire(),
                request.getEtat()
            ).send();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Ship arrival event recorded on blockchain");
            response.put("transactionHash", receipt.getTransactionHash());
            response.put("blockNumber", receipt.getBlockNumber());
            response.put("data", request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing ship arrival contract", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to record ship arrival: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/dap")
    public ResponseEntity<Map<String, Object>> testDapContract(@RequestBody DapRequest request) {
        try {
            // Create credentials from private key
            Credentials credentials = Credentials.create(privateKey);

            // Load the contract
            DAPContract contract = DAPContract.load(
                dapContractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
            );

            // Convert timestamp to BigInteger
            BigInteger requestDate = request.getRequestDate() != null ? 
                BigInteger.valueOf(request.getRequestDate()) : BigInteger.valueOf(Instant.now().getEpochSecond());

            // Call the contract function
            TransactionReceipt receipt = contract.recordDAP(
                BigInteger.valueOf(request.getNoticeNumber()),
                request.getCallNumber(),
                request.getPortName(),
                request.getPortCode(),
                request.getShipName(),
                request.getOperatorName(),
                request.getStatus(),
                requestDate
            ).send();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "DAP event recorded on blockchain");
            response.put("transactionHash", receipt.getTransactionHash());
            response.put("blockNumber", receipt.getBlockNumber());
            response.put("data", request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing DAP contract", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Failed to record DAP: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 