package com.berexia.business.event.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for ship arrival events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipArrivalEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The ship ID
     */
    @NotBlank(message = "Ship ID is required")
    private Long numeroAvis;
    
    /**
     * The ship name
     */
    @NotBlank(message = "Ship name is required")
    private String numeroEscale;
    
    /**
     * The call ID
     */
    @NotBlank(message = "Call ID is required")
    private String nomPort;
    
    /**
     * The port ID
     */
    @NotBlank(message = "Port ID is required")
    private String codePort;
    
    /**
     * The port name
     */
    @NotBlank(message = "Port name is required")
    private String nomNavire;
    
    /**
     * The operator name
     */
    @NotBlank(message = "Operator name is required")
    private String etat;

    /**
     * The actual arrival time
     */
    private LocalDateTime dateArrivee;
} 