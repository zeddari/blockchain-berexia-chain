package com.berexia.orchestrator.services;

import com.berexia.orchestrator.dto.AvisArriveeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service for interacting with the Ship Management Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShipManagementService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${services.ship-management.url}")
    private String shipManagementServiceUrl;
    
    /**
     * Sends a ship arrival notice to the Ship Management Service.
     * 
     * @param requestPayload the payload containing the ship arrival notice
     * @return the response from the Ship Management Service
     */
    public ResponseEntity<String> notifyShipArrival(Map<String, Object> requestPayload) {
        try {
            // Convert Map to AvisArriveeDto
            AvisArriveeDto dto = objectMapper.convertValue(requestPayload, AvisArriveeDto.class);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the HTTP entity
            HttpEntity<AvisArriveeDto> requestEntity = new HttpEntity<>(dto, headers);

            // Call the Ship Management Service API
            String apiUrl = shipManagementServiceUrl.trim() + "/api/notify/arrival";
            log.info("Calling Ship Arrival API at: {}", apiUrl);
            return restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        } catch (Exception e) {
            log.error("Error calling Ship Arrival API", e);
            throw new RuntimeException("Failed to call Ship Arrival API: " + e.getMessage(), e);
        }
    }
} 