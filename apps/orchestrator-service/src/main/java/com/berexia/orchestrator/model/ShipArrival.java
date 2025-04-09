package com.berexia.orchestrator.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipArrival {
    private String callId;
    private String shipName;
    private String imoNumber;
    private String portOfArrival;
    private LocalDateTime arrivalDateTime;
    private String status;
    private String remarks;
} 