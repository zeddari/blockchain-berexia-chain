package com.berexia.business.event.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for DAP (Demande d'Autorisation Préalable) requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DAPRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The notice number for the DAP request
     */
    private Long noticeNumber;

    /**
     * The call number associated with the ship's port call
     */
    private String callNumber;

    /**
     * The name of the port
     */
    private String portName;

    /**
     * The code of the port
     */
    private String portCode;

    /**
     * The name of the ship
     */
    private String shipName;

    /**
     * The name of the ship operator
     */
    private String operatorName;

    /**
     * The current status of the DAP request
     */
    private String status;

    /**
     * The date when the request was made
     */
    private LocalDateTime requestDate;
} 