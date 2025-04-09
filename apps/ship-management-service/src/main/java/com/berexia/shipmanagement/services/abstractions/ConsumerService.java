package com.berexia.shipmanagement.services.abstractions;


/**
 * Service interface for handling operations related to consuming messages.
 *
 * @param <T> the type of the message to be consumed
 */
public interface ConsumerService<T> {

    /**
     * Consumes the provided message.
     *
     * @param message the message to be consumed
     */
    void consume(T message);
}