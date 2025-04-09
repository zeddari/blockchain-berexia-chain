package com.portnet.dockchain.service;

import com.portnet.dockchain.entity.DapDemande;
import com.portnet.dockchain.repository.DapDemandeRepository;
import org.springframework.stereotype.Service;

@Service
public class DapDemandeService {

    private final DapDemandeRepository repository;

    public DapDemandeService(DapDemandeRepository repository) {
        this.repository = repository;
    }

    public DapDemande save(DapDemande demande) {
        return repository.save(demande);
    }
}