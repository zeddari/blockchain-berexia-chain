package com.berexia.shipmanagement.controllers;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.exceptions.TradeChainAvailabilityException;
import com.berexia.shipmanagement.services.abstractions.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notify")
public class NoticeController {

    private final SupplierService<AvisArriveeDto> aaMessagingService;

    @Autowired
    public NoticeController(SupplierService<AvisArriveeDto> aaMessagingService) {
        this.aaMessagingService = aaMessagingService;
    }

    @PostMapping("/arrival")
    @Operation(summary = "Avis d'arrivée", description = "Ce endpoint est exposé pour que PortNet communique à TradeChain un avis d'arrivée")
    public ResponseEntity<String> notifyArrival(@RequestBody @Valid AvisArriveeDto avieArriveeDto) {
        log.info("Request received for queueing Avis d'arrivée: {}", avieArriveeDto);
        try {
            if (aaMessagingService.publishAA(avieArriveeDto))
                return new ResponseEntity<>("Le message est bien publié", HttpStatus.OK);
            else
                return new ResponseEntity<>("Une erreur s'est produite et le message n'a pas été envoyé", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TradeChainAvailabilityException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PostMapping("/send-message")
    @Operation(summary = "Send a message", description = "Ce endpoint est exposé pour tester l'envoie de message dans le queue")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        try {
            if (aaMessagingService.send(message))
                return new ResponseEntity<>("Le message est bien publié", HttpStatus.OK);
            else
                return new ResponseEntity<>("Une erreur s'est produite et le message n'a pas été envoyé", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TradeChainAvailabilityException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
}
