package com.portnet.dockchain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class DAPRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Le numéro d'avis est obligatoire")
    private Long numeroAvis;

    @NotNull(message = "Le numéro d'escale est obligatoire")
    @Size(min = 1, message = "Le numéro d'escale ne peut pas être vide")
    private String numeroEscale;

    @NotNull(message = "Le nom du port est obligatoire")
    @Size(min = 1, message = "Le nom du port ne peut pas être vide")
    private String nomPort;

    @NotNull(message = "Le code du port est obligatoire")
    @Size(min = 1, message = "Le code du port ne peut pas être vide")
    private String codePort;

    @NotNull(message = "Le nom du navire est obligatoire")
    @Size(min = 1, message = "Le nom du navire ne peut pas être vide")
    private String nomNavire;

    @NotNull(message = "Le nom de l'opérateur est obligatoire")
    @Size(min = 1, message = "Le nom de l'opérateur ne peut pas être vide")
    private String nomOperateur;

    @NotNull(message = "L'état est obligatoire")
    @Size(min = 1, message = "L'état ne peut pas être vide")
    private String etat;

    // Getters et Setters

    public Long getNumeroAvis() {
        return numeroAvis;
    }

    public void setNumeroAvis(Long numeroAvis) {
        this.numeroAvis = numeroAvis;
    }

    public String getNumeroEscale() {
        return numeroEscale;
    }

    public void setNumeroEscale(String numeroEscale) {
        this.numeroEscale = numeroEscale;
    }

    public String getNomPort() {
        return nomPort;
    }

    public void setNomPort(String nomPort) {
        this.nomPort = nomPort;
    }

    public String getCodePort() {
        return codePort;
    }

    public void setCodePort(String codePort) {
        this.codePort = codePort;
    }

    public String getNomNavire() {
        return nomNavire;
    }

    public void setNomNavire(String nomNavire) {
        this.nomNavire = nomNavire;
    }

    public String getNomOperateur() {
        return nomOperateur;
    }

    public void setNomOperateur(String nomOperateur) {
        this.nomOperateur = nomOperateur;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
}