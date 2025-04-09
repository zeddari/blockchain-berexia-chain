package com.berexia.shipmanagement.services;

    import com.berexia.shipmanagement.dtos.AvisArriveeDto;
    import com.berexia.shipmanagement.entities.AvisArrivee;
    import com.berexia.shipmanagement.services.abstractions.AvisArriveeService;
    import com.berexia.shipmanagement.services.abstractions.ConsumerService;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.amqp.rabbit.annotation.RabbitListener;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    /**
     * Service implementation for consuming AvisArrivee messages from a RabbitMQ queue.
     */
    @Service
    @Slf4j
    public class AvisArriveeMessageConsumer implements ConsumerService<AvisArriveeDto> {

        private final AvisArriveeService avisArriveeService;

        /**
         * Constructs a new AvisArriveeMessageConsumer with the specified AvisArriveeService.
         *
         * @param avisArriveeService the service to be used for saving AvisArrivee entities
         */
        @Autowired
        public AvisArriveeMessageConsumer(AvisArriveeService avisArriveeService) {
            this.avisArriveeService = avisArriveeService;
        }

        /**
         * Consumes an AvisArriveeDto message from the configured RabbitMQ queue.
         * Saves the message as an AvisArrivee entity and logs the process.
         *
         * @param avieArriveeDto the data transfer object containing the details of the AvisArrivee to be consumed
         */
        @Override
        @RabbitListener(queues = "${rabbitmq.aa.queue}")
        @Transactional
        public void consume(AvisArriveeDto avieArriveeDto) {
            log.info("Message réçu: {}", avieArriveeDto);
            AvisArrivee avisArrivee = avisArriveeService.save(avieArriveeDto);
            log.info("Avis d'arrivée bien inséré: {}", avisArrivee);
        }
    }