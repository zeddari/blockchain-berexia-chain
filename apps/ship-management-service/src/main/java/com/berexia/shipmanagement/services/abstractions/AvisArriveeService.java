package com.berexia.shipmanagement.services.abstractions;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.entities.AvisArrivee;

/**
 * Service interface for handling operations related to AvisArrivee.
 */
public interface AvisArriveeService {

    /**
     * Saves a new AvisArrivee entity based on the provided DTO.
     *
     * @param avisArriveeDto the data transfer object containing the details of the AvisArrivee to be saved
     * @return the saved AvisArrivee entity
     */
    AvisArrivee save(AvisArriveeDto avisArriveeDto);
}