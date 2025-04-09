package com.portnet.dockchain.service;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    private final ConnectionFactory connectionFactory;

    public HealthCheckService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public boolean checkRabbitMQHealth() {
        try {
            connectionFactory.createConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}