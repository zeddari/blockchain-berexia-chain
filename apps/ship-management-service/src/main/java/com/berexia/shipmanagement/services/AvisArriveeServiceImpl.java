package com.berexia.shipmanagement.services;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.entities.AvisArrivee;
import com.berexia.shipmanagement.exceptions.TradeChainValidationException;
import com.berexia.shipmanagement.repositories.AvisArriveeRepository;
import com.berexia.shipmanagement.services.abstractions.AvisArriveeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling operations related to AvisArrivee.
 */
@Service
@Slf4j
public class AvisArriveeServiceImpl implements AvisArriveeService {

    private final AvisArriveeRepository avisArriveeRepository;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new AvisArriveeServiceImpl with the specified repository and object mapper.
     *
     * @param avisArriveeRepository the repository to be used for saving AvisArrivee entities
     * @param objectMapper the object mapper to be used for converting DTOs to entities
     */
    @Autowired
    public AvisArriveeServiceImpl(AvisArriveeRepository avisArriveeRepository, ObjectMapper objectMapper) {
        this.avisArriveeRepository = avisArriveeRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Saves a new AvisArrivee entity based on the provided DTO.
     *
     * @param avisArriveeDto the data transfer object containing the details of the AvisArrivee to be saved
     * @return the saved AvisArrivee entity
     */
    @Override
    public AvisArrivee save(AvisArriveeDto avisArriveeDto) {
        try {
            if(avisArriveeDto == null) {
                throw new IllegalArgumentException("Argument 'avisArriveeDto' cannot be null");
            }

            AvisArrivee avisArrivee = objectMapper.convertValue(avisArriveeDto, AvisArrivee.class);
            return avisArriveeRepository.save(avisArrivee);
        } catch (ConstraintViolationException e) {
            String message = "Échec de la validation lors de l'enregistrement de l'Avis d'Arrivée" + avisArriveeDto;
            log.error(message, e);
            throw new TradeChainValidationException(message, e);
        }

    }
}