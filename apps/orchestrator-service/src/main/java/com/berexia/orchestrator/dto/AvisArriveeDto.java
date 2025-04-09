package com.berexia.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvisArriveeDto implements Serializable {

    private Long numeroAvis;
    private String numeroEscale;
    private String nomPort;
    private String codePort;
    private String nomNavire;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateArrivee;
    private String etat;
} 