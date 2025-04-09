package com.berexia.shipmanagement.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="avis_arrivee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvisArrivee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private long numeroAvis;
    @NotNull
    private String numeroEscale;
    @NotNull
    private String nomPort;
    @NotNull
    private String codePort;
    @NotNull
    private String nomNavire;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateArrivee;
    @NotNull
    // This should be an ENUM. What are the other possible states?
    private String etat;
}
