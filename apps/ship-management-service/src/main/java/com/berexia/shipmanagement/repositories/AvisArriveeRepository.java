package com.berexia.shipmanagement.repositories;

import com.berexia.shipmanagement.entities.AvisArrivee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisArriveeRepository extends JpaRepository<AvisArrivee, Long> {
}
