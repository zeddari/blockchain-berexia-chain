package com.portnet.dockchain.service;

import com.portnet.dockchain.entity.DapDemande;
import com.portnet.dockchain.repository.DapDemandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DapDemandeServiceTest {

    private DapDemandeRepository repository;
    private DapDemandeService service;

    @BeforeEach
    void setUp() {
        repository = mock(DapDemandeRepository.class);
        service = new DapDemandeService(repository);
    }

    @Test
    void shouldSaveDapDemande() {
        DapDemande demande = new DapDemande();
        demande.setId(1L);
        demande.setNumeroAvis(1122334455L);
        when(repository.save(any(DapDemande.class))).thenReturn(demande);

        DapDemande saved = service.save(demande);

        ArgumentCaptor<DapDemande> captor = ArgumentCaptor.forClass(DapDemande.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(demande);
        assertThat(saved).isEqualTo(demande);
    }
}