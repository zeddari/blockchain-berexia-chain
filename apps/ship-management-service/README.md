# Ship Management Service

## Overview
The Ship Management Service is a Spring Boot application designed to handle ship arrival notifications in a trade chain system. It serves as an integration point between PortNet and the TradeChain blockchain-based solution.

## Features
- Receive and process ship arrival notices ("Avis d'Arrivée")
- Message queuing with RabbitMQ for asynchronous processing
- Integration with Quorum blockchain for data persistence
- RESTful API endpoints for notification delivery

## Technology Stack
- Java 17
- Spring Boot 3.4.4
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- MySQL Database
- Lombok for boilerplate code reduction
- Quorum Blockchain integration using Web3j

## API Endpoints
- `POST /api/notify/arrival`: Receives ship arrival notices from PortNet
- `POST /api/notify/send-message`: Test endpoint for message queue functionality

## Domain Model
The service handles "Avis d'Arrivée" (Arrival Notice) with the following properties:
- Arrival notice number (numeroAvis)
- Call number (numeroEscale)
- Port name (nomPort)
- Port code (codePort)
- Ship name (nomNavire)
- Arrival date (dateArrivee)
- Status (etat)

## Configuration
The service is configured to connect to:
- MySQL database
- RabbitMQ message broker
- Quorum blockchain node

## Building and Running
This service is built using Maven. Use the following commands:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Dependencies
See the `pom.xml` file for a complete list of dependencies. 
