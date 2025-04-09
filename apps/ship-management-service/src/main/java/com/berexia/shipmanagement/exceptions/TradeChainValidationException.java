package com.berexia.shipmanagement.exceptions;

public class TradeChainValidationException extends RuntimeException {

    public TradeChainValidationException(String message) {
        super(message);
    }

    public TradeChainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
