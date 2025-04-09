package com.portnet.dockchain.web3;

import com.portnet.dockchain.entity.DapDemande;
import com.portnet.dockchain.model.DAPRequestDto;
import com.portnet.dockchain.service.BlockchainService;
import com.portnet.dockchain.service.DapDemandeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DapMessageListenerTest {

    private DapDemandeService dapDemandeService;
    private BlockchainService blockchainService;
    private DapMessageListener dapMessageListener;

    @BeforeEach
    void setUp() {
        dapDemandeService = mock(DapDemandeService.class);
        blockchainService = mock(BlockchainService.class);
        dapMessageListener = new DapMessageListener(dapDemandeService, blockchainService);
    }

    @Test
    void shouldHandleDapMessageSuccessfully() {
        DAPRequestDto dto = new DAPRequestDto();
        dto.setNumeroAvis(1122334455L);
        dto.setNumeroEscale("ESC98765");
        dto.setNomPort("Port Tanger Med");
        dto.setCodePort("MATNG");
        dto.setNomNavire("Navire Neptune");
        dto.setNomOperateur("Operateur Maritime ABC");
        dto.setEtat("En attente");

        DapDemande savedDemande = new DapDemande();
        savedDemande.setId(1L);
        when(dapDemandeService.save(any(DapDemande.class))).thenReturn(savedDemande);

        dapMessageListener.handleDapMessage(dto);

        verify(dapDemandeService, times(1)).save(any(DapDemande.class));
        verify(blockchainService, times(1)).enregistrerDemandeSurBlockchain(savedDemande);
    }
}