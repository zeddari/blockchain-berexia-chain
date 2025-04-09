package com.portnet.dockchain.service;

import com.portnet.dockchain.entity.DapDemande;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    public void enregistrerDemandeSurBlockchain(DapDemande demande) {
        try {
            logger.info("Appel du smart contract pour la demande : {}", demande.getNumeroAvis());

            // appel smartcontract.

            logger.info("Demande enregistrée sur la blockchain.");
        } catch (Exception e) {
            logger.error("Erreur lors de l'appel au smart contract : {}", e.getMessage(), e);
        }
    }
}