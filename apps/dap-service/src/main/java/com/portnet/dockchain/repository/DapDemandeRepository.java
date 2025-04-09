package com.portnet.dockchain.repository;

import com.portnet.dockchain.entity.DapDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DapDemandeRepository extends JpaRepository<DapDemande, Long> {
    boolean existsByNumeroAvis(Long numeroAvis);
    Optional<DapDemande> findByNumeroAvis(Long numeroAvis);
}