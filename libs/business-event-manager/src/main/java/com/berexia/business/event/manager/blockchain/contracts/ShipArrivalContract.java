package com.berexia.business.event.manager.blockchain.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Smart contract wrapper for Ship Arrival operations.
 */
public class ShipArrivalContract extends Contract {
    private static final String BINARY = ""; // Contract binary will be filled by Web3j

    public static final String FUNC_RECORDSHIPARRIVAL = "recordShipArrival";
    public static final String FUNC_UPDATESHIPARRIVAL = "updateShipArrival";
    public static final String FUNC_GETSHIPARRIVAL = "getShipArrival";
    public static final String FUNC_SHIPARRIVALEXISTS = "shipArrivalExists";
    public static final String FUNC_GETSHIPEVENTS = "getShipEvents";
    public static final String FUNC_GETPORTEVENTS = "getPortEvents";

    public ShipArrivalContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    public ShipArrivalContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    /**
     * Records a new ship arrival event on the blockchain.
     * @return The event ID of the recorded event
     */
    public RemoteFunctionCall<TransactionReceipt> recordShipArrival(
            BigInteger numeroAvis,
            String numeroEscale,
            String nomPort,
            String codePort,
            String nomNavire,
            String etat) {
        final Function function = new Function(
                FUNC_RECORDSHIPARRIVAL,
                Arrays.asList(
                        new Uint256(numeroAvis),
                        new org.web3j.abi.datatypes.Utf8String(numeroEscale),
                        new org.web3j.abi.datatypes.Utf8String(nomPort),
                        new org.web3j.abi.datatypes.Utf8String(codePort),
                        new org.web3j.abi.datatypes.Utf8String(nomNavire),
                        new org.web3j.abi.datatypes.Utf8String(etat)
                ),
                Collections.emptyList()
        );
        return executeRemoteCallTransaction(function);
    }

    /**
     * Updates an existing ship arrival event.
     */
    public RemoteFunctionCall<TransactionReceipt> updateShipArrival(
            BigInteger numeroAvis,
            String numeroEscale,
            String nomPort,
            String codePort,
            String nomNavire,
            String etat) {
        final Function function = new Function(
                FUNC_UPDATESHIPARRIVAL,
                Arrays.asList(
                        new Uint256(numeroAvis),
                        new org.web3j.abi.datatypes.Utf8String(numeroEscale),
                        new org.web3j.abi.datatypes.Utf8String(nomPort),
                        new org.web3j.abi.datatypes.Utf8String(codePort),
                        new org.web3j.abi.datatypes.Utf8String(nomNavire),
                        new org.web3j.abi.datatypes.Utf8String(etat)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * Gets a ship arrival event by numeroAvis.
     */
    public RemoteFunctionCall<TransactionReceipt> getShipArrival(BigInteger numeroAvis) {
        final Function function = new Function(
                FUNC_GETSHIPARRIVAL,
                Arrays.asList(new Uint256(numeroAvis)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * Checks if a ship arrival event exists.
     */
    public RemoteFunctionCall<Boolean> shipArrivalExists(BigInteger numeroAvis) {
        final Function function = new Function(
                FUNC_SHIPARRIVALEXISTS,
                Arrays.asList(new Uint256(numeroAvis)),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    /**
     * Gets all event IDs for a ship.
     */
    public RemoteFunctionCall<byte[][]> getShipEvents(String shipId) {
        final Function function = new Function(
                FUNC_GETSHIPEVENTS,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(shipId)),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.DynamicArray<Bytes32>>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[][].class);
    }

    /**
     * Gets all event IDs for a port.
     */
    public RemoteFunctionCall<byte[][]> getPortEvents(String portId) {
        final Function function = new Function(
                FUNC_GETPORTEVENTS,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(portId)),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.DynamicArray<Bytes32>>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[][].class);
    }

    /**
     * Loads a deployed ShipArrival contract.
     */
    public static ShipArrivalContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ShipArrivalContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    /**
     * Loads a deployed ShipArrival contract.
     */
    public static ShipArrivalContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ShipArrivalContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }
} 