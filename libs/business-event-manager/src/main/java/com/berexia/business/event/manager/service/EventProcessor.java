package com.berexia.business.event.manager.service;

/**
 * Generic interface for event processors.
 * 
 * @param <T> the type of event to process
 */
public interface EventProcessor<T> {

    /**
     * Process an event.
     * 
     * @param event the event to process
     * @return true if the event was processed successfully, false otherwise
     */
    boolean process(T event);
} 