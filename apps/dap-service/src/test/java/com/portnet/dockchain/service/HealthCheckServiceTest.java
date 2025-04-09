package com.portnet.dockchain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HealthCheckServiceTest {

    private ConnectionFactory connectionFactory;
    private HealthCheckService healthCheckService;

    @BeforeEach
    void setUp() {
        connectionFactory = mock(ConnectionFactory.class);
        healthCheckService = new HealthCheckService(connectionFactory);
    }

    @Test
    void shouldReturnTrueWhenRabbitIsHealthy() {
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);
        boolean result = healthCheckService.checkRabbitMQHealth();
        assertThat(result).isTrue();
        verify(connectionFactory).createConnection();
        verify(connection).close();
    }

    @Test
    void shouldReturnFalseWhenRabbitIsDown() {
        when(connectionFactory.createConnection()).thenThrow(new RuntimeException("RabbitMQ down"));
        boolean result = healthCheckService.checkRabbitMQHealth();
        assertThat(result).isFalse();
        verify(connectionFactory).createConnection();
    }
}