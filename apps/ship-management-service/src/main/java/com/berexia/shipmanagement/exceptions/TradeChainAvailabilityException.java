package com.berexia.shipmanagement.exceptions;

public class TradeChainAvailabilityException extends RuntimeException  {

    public TradeChainAvailabilityException(String message) {
        super(message);
    }

    public TradeChainAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

}
