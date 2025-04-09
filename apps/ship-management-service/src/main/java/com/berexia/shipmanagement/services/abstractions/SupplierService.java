package com.berexia.shipmanagement.services.abstractions;


/**
 * Service interface for handling operations related to Supplier.
 */
public interface SupplierService<T> {

    /**
     * Sends a message.
     *
     * @param message the message to be sent
     * @return true if the message was sent successfully, false otherwise
     */
    boolean send(String message);

    /**
     * Publishes an AvisArrivee.
     *
     * @param message the data transfer object containing the details of the message to be published
     * @return true if the message was published successfully, false otherwise
     */
    boolean publishAA(T message);
}