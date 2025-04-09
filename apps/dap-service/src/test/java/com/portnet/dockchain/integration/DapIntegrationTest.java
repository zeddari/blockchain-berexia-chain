package com.portnet.dockchain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portnet.dockchain.entity.DapDemande;
import com.portnet.dockchain.model.DAPRequestDto;
import com.portnet.dockchain.repository.DapDemandeRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class DapIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DapDemandeRepository dapDemandeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldProcessDapEndToEnd() throws Exception {
        dapDemandeRepository.deleteAll();

        DAPRequestDto dto = new DAPRequestDto();
        dto.setNumeroAvis(1122334455L);
        dto.setNumeroEscale("ESC98765");
        dto.setNomPort("Port Tanger Med");
        dto.setCodePort("MATNG");
        dto.setNomNavire("Navire Neptune");
        dto.setNomOperateur("Operateur Maritime ABC");
        dto.setEtat("En attente");

        // Appel vers l API
        mockMvc.perform(post("/api/v1/dap")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        // Vérif de la DB
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<DapDemande> saved = dapDemandeRepository.findByNumeroAvis(1122334455L);
                    assertThat(saved).isPresent();
                    assertThat(saved.get().getNomPort()).isEqualTo("Port Tanger Med");
                });
    }
}