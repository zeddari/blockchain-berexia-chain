package com.berexia.shipmanagement.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvisArriveeDto implements Serializable {

    @NotNull(message = "Le numero d'avis est requis")
    private long numeroAvis;
    @NotBlank(message = "Le numero descale est requis")
    private String numeroEscale;
    @NotBlank(message = "Le nom de port est requis")
    private String nomPort;
    @NotBlank(message = "Le code de port est requis")
    private String codePort;
    @NotBlank(message = "Le nom de navire est requis")
    private String nomNavire;
    @NotNull(message = "La date d'arrivée est requise")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateArrivee;
    // This should be an ENUM. What are the other possible states?
    @NotBlank(message = "L'état actuel est requis")
    private String etat;
}
