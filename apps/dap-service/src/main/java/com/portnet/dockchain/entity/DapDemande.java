package com.portnet.dockchain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "dap_demande")
@Data
public class DapDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_avis", unique = true, nullable = false)
    private Long numeroAvis;

    @Column(name = "numero_escale", nullable = false)
    private String numeroEscale;

    @Column(name = "nom_port", nullable = false)
    private String nomPort;

    @Column(name = "code_port", nullable = false)
    private String codePort;

    @Column(name = "nom_navire", nullable = false)
    private String nomNavire;

    @Column(name = "nom_operateur", nullable = false)
    private String nomOperateur;

    @Column(name = "etat", nullable = false)
    private String etat;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}