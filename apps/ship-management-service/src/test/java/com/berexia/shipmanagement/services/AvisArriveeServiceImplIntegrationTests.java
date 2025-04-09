package com.berexia.shipmanagement.services;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.entities.AvisArrivee;
import com.berexia.shipmanagement.exceptions.TradeChainValidationException;
import com.berexia.shipmanagement.repositories.AvisArriveeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the AvisArriveeServiceImpl class.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class AvisArriveeServiceImplIntegrationTests {

    @Autowired
    private AvisArriveeServiceImpl avisArriveeService;

    @Autowired
    private AvisArriveeRepository avisArriveeRepository;

    /**
     * Tests saving and retrieving an AvisArrivee entity.
     */
    @Test
    void testSaveAndRetrieveAvisArrivee() {

        LocalDateTime testDate = LocalDateTime.now();

        // Given: Creating and saving the entity
        AvisArrivee avisArrivee = AvisArrivee.builder()
                .numeroAvis(1L)
                .numeroEscale("Test Escale")
                .nomPort("Test Port")
                .codePort("Test Code")
                .nomNavire("Test Arrivee")
                .dateArrivee(testDate)
                .etat("Test Etat")
                .build();

        avisArrivee = avisArriveeRepository.save(avisArrivee);

        // When: Retrieving the saved entity
        Optional<AvisArrivee> foundAvis = avisArriveeRepository.findById(avisArrivee.getId());

        // Then: Validate that the entity is correctly saved and retrieved
        assertTrue(foundAvis.isPresent());
        assertEquals(1L, foundAvis.get().getNumeroAvis());
        assertEquals(testDate, foundAvis.get().getDateArrivee());
        assertEquals("Test Arrivee", foundAvis.get().getNomNavire());
        assertEquals("Test Escale", foundAvis.get().getNumeroEscale());
        assertEquals("Test Port", foundAvis.get().getNomPort());
        assertEquals("Test Code", foundAvis.get().getCodePort());
        assertEquals("Test Etat", foundAvis.get().getEtat());

    }

    /**
     * Tests saving an AvisArriveeDto with null attributes and expects a TradeChainValidationException.
     */
    @Test
    void testSavingAvisArriveeDtoWithNullAttributes() {

        AvisArriveeDto avisArriveeDto = new AvisArriveeDto();
        avisArriveeDto.setNumeroAvis(1L);

        try {
            // When: Saving the DTO
            avisArriveeService.save(avisArriveeDto);
        } catch (TradeChainValidationException e) {
            assertEquals("Échec de la validation lors de l'enregistrement de l'Avis d'Arrivée" + avisArriveeDto, e.getMessage());
        }
    }
}