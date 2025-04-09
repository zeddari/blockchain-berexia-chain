package com.portnet.dockchain.controller;

import com.portnet.dockchain.model.DAPRequestDto;
import com.portnet.dockchain.model.ApiResponse;
import com.portnet.dockchain.service.HealthCheckService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dap")
@Validated
public class DapController {

    private static final Logger logger = LoggerFactory.getLogger(DapController.class);

    private final RabbitTemplate rabbitTemplate;
    private final HealthCheckService healthCheckService;

    public DapController(RabbitTemplate rabbitTemplate, HealthCheckService healthCheckService) {
        this.rabbitTemplate = rabbitTemplate;
        this.healthCheckService = healthCheckService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createDap(@Valid @RequestBody DAPRequestDto dapRequestDto) {
        logger.info("Réception d'une nouvelle demande DAP : {}", dapRequestDto);

        boolean isRabbitMqHealthy = healthCheckService.checkRabbitMQHealth();
        if (!isRabbitMqHealthy) {
            logger.warn("RabbitMQ est indisponible.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse("FAILURE", "RabbitMQ is unavailable"));
        }

        try {
            rabbitTemplate.convertAndSend("queue.dap.demand", dapRequestDto);
            logger.info("Demande DAP envoyée dans la queue avec succès.");
            return ResponseEntity.ok(new ApiResponse("SUCCESS", "Demande DAP placée en file d'attente avec succès."));
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la demande dans RabbitMQ", e);
            throw new RuntimeException("Erreur lors de l'envoi de la demande dans la queue", e);
        }
    }
}