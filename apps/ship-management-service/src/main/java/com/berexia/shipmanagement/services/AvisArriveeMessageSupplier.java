package com.berexia.shipmanagement.services;

import com.berexia.shipmanagement.dtos.AvisArriveeDto;
import com.berexia.shipmanagement.exceptions.TradeChainAvailabilityException;
import com.berexia.shipmanagement.services.abstractions.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling operations related to sending messages and publishing AvisArrivee.
 */
@Slf4j
@Service
public class AvisArriveeMessageSupplier implements SupplierService<AvisArriveeDto> {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.aa.exchange}")
    private String aaExchange;

    @Value("${rabbitmq.aa.binding}")
    private String binding;

    /**
     * Constructs a new AvisArriveeMessageSupplier with the specified RabbitTemplate.
     *
     * @param rabbitTemplate the RabbitTemplate to be used for sending messages
     */
    @Autowired
    public AvisArriveeMessageSupplier(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a message to the configured RabbitMQ exchange and binding.
     *
     * @param message the message to be sent
     * @return true if the message was sent successfully, false otherwise
     * @throws TradeChainAvailabilityException if the RabbitMQ server is not available
     */
    @Override
    public boolean send(String message) {
        try {
            rabbitTemplate.convertAndSend(aaExchange, binding + ".arrival", message);
            return true;
        } catch (AmqpException ex) {
            throw new TradeChainAvailabilityException("Our queueing server is not available currently, please try again later", ex);
        } catch (Exception ex) {
            log.error("An Error occurred while sending the following message {}", message, ex);
            return false;
        }
    }

    /**
     * Publishes an AvisArrivee message to the configured RabbitMQ exchange and binding.
     *
     * @param avieArriveeDto the data transfer object containing the details of the AvisArrivee to be published
     * @return true if the AvisArrivee was published successfully, false otherwise
     * @throws TradeChainAvailabilityException if the RabbitMQ server is not available
     */
    @Override
    public boolean publishAA(AvisArriveeDto avieArriveeDto) {
        try {
            log.info("Sending message: {} to AAQueue", avieArriveeDto);
            rabbitTemplate.convertAndSend(aaExchange, binding + ".arrival", avieArriveeDto);
            log.info("Avis d'arrivée message was published successfully : {}", avieArriveeDto);
            return true;
        } catch (AmqpException ex) {
            log.error("RabbitMQ server is not available currently", ex);
            throw new TradeChainAvailabilityException("Notre serveur de file d'attente n'est pas disponible actuellement, veuillez réessayer plus tard", ex);
        } catch (Exception ex) {
            log.error("An Error occurred while sending the following message ");
            return false;
        }
    }
}