package com.portnet.dockchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portnet.dockchain.model.DAPRequestDto;
import com.portnet.dockchain.service.HealthCheckService;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DapController.class)
class DapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private HealthCheckService healthCheckService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDapSuccessfully() throws Exception {
        DAPRequestDto dto = new DAPRequestDto();
        dto.setNumeroAvis(1122334455L);
        dto.setNumeroEscale("ESC98765");
        dto.setNomPort("Port Tanger Med");
        dto.setCodePort("MATNG");
        dto.setNomNavire("Navire Neptune");
        dto.setNomOperateur("Operateur Maritime ABC");
        dto.setEtat("En attente");

        when(healthCheckService.checkRabbitMQHealth()).thenReturn(true);

        mockMvc.perform(post("/api/v1/dap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(rabbitTemplate).convertAndSend(eq("queue.dap.demand"),
                argThat((DAPRequestDto dap) -> dap.getNumeroAvis().equals(1122334455L)));
    }

    @Test
    void shouldReturnServiceUnavailableWhenRabbitMQIsDown() throws Exception {
        DAPRequestDto dto = new DAPRequestDto();
        dto.setNumeroAvis(1122334455L);
        dto.setNumeroEscale("ESC98765");
        dto.setNomPort("Port Tanger Med");
        dto.setCodePort("MATNG");
        dto.setNomNavire("Navire Neptune");
        dto.setNomOperateur("Operateur Maritime ABC");
        dto.setEtat("En attente");

        when(healthCheckService.checkRabbitMQHealth()).thenReturn(false);

        mockMvc.perform(post("/api/v1/dap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("FAILURE"));

        // Ne PAS faire de verify sur convertAndSend ici
        verifyNoInteractions(rabbitTemplate);
    }
}