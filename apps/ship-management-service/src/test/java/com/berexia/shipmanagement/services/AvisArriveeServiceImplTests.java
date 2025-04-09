package com.berexia.shipmanagement.services;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.entities.AvisArrivee;
import com.berexia.shipmanagement.repositories.AvisArriveeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the AvisArriveeServiceImpl class.
 */
public class AvisArriveeServiceImplTests {

    @Mock
    private AvisArriveeRepository avisArriveeRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AvisArriveeServiceImpl avisArriveeServiceImpl;

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the save method to ensure it returns the saved entity when the DTO is valid.
     */
    @Test
    void save() {
        AvisArriveeDto dto = new AvisArriveeDto();
        AvisArrivee entity = new AvisArrivee();
        when(objectMapper.convertValue(dto, AvisArrivee.class)).thenReturn(entity);
        when(avisArriveeRepository.save(any(AvisArrivee.class))).thenReturn(entity);

        AvisArrivee result = avisArriveeServiceImpl.save(dto);

        assertEquals(entity, result);
    }

    /**
     * Tests the save method to ensure it throws an exception when the DTO is null.
     */
    @Test
    void save_shouldThrowException_whenDtoIsNull() {
        try {
            avisArriveeServiceImpl.save(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Argument 'avisArriveeDto' cannot be null", e.getMessage());
        }
    }
}