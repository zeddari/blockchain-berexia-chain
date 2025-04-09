package com.berexia.business.event.manager.blockchain.contracts;

import java.math.BigInteger;
import java.util.Arrays;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Smart contract wrapper for DAP (Demande d'Autorisation Préalable) operations.
 */
public class DAPContract extends Contract {
    private static final String BINARY = ""; // Contract binary will be filled by Web3j

    public static final String FUNC_RECORDDAP = "recordDAP";
    public static final String FUNC_UPDATEDAPSTATUS = "updateDAPStatus";
    public static final String FUNC_DAPEXISTS = "dapExists";

    public DAPContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    public DAPContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    /**
     * Records a new DAP request on the blockchain.
     */
    public RemoteFunctionCall<TransactionReceipt> recordDAP(
            BigInteger noticeNumber,
            String callNumber,
            String portName,
            String portCode,
            String shipName,
            String operatorName,
            String status,
            BigInteger requestDate) {
        final Function function = new Function(
                FUNC_RECORDDAP,
                Arrays.asList(
                        new Uint256(noticeNumber),
                        new org.web3j.abi.datatypes.Utf8String(callNumber),
                        new org.web3j.abi.datatypes.Utf8String(portName),
                        new org.web3j.abi.datatypes.Utf8String(portCode),
                        new org.web3j.abi.datatypes.Utf8String(shipName),
                        new org.web3j.abi.datatypes.Utf8String(operatorName),
                        new org.web3j.abi.datatypes.Utf8String(status),
                        new Uint256(requestDate)),
                Arrays.asList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * Updates the status of a DAP request.
     */
    public RemoteFunctionCall<TransactionReceipt> updateDAPStatus(BigInteger noticeNumber, String status) {
        final Function function = new Function(
                FUNC_UPDATEDAPSTATUS,
                Arrays.asList(
                        new Uint256(noticeNumber),
                        new org.web3j.abi.datatypes.Utf8String(status)),
                Arrays.asList());
        return executeRemoteCallTransaction(function);
    }

    /**
     * Checks if a DAP request exists.
     */
    public RemoteFunctionCall<Boolean> dapExists(BigInteger noticeNumber) {
        final Function function = new Function(
                FUNC_DAPEXISTS,
                Arrays.asList(new org.web3j.abi.datatypes.generated.Uint256(noticeNumber)),
                Arrays.asList(new TypeReference<org.web3j.abi.datatypes.Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    /**
     * Loads a deployed DAP contract.
     */
    public static DAPContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DAPContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    /**
     * Loads a deployed DAP contract.
     */
    public static DAPContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DAPContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }
} 