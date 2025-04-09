# Business Event Manager

A lightweight library for consuming and processing business events via RabbitMQ in the TradeChain ecosystem, specifically handling ship arrivals and docking requests (DAP).

## Overview

The Business Event Manager is a Spring Boot library that:

1. Consumes business events from RabbitMQ queues
2. Processes these events through a pluggable processor architecture
3. Persists event data to a relational database
4. Optionally records events on a blockchain for immutability and auditability

## Architecture

The library is built around these key components:

- **Message Listeners**: Components that consume messages from RabbitMQ queues
- **Event Processors**: Strategy pattern implementations that process specific event types
- **Domain Model**: JPA entities that represent business objects
- **DTOs**: Data Transfer Objects for message serialization/deserialization
- **Repositories**: Spring Data JPA repositories for data access
- **Blockchain Connector**: Service for recording events on a blockchain (optional)

## Features

- Automatic persistence of event data
- Error handling and recovery
- Conditional enablement of consumers
- Blockchain integration for immutable event recording (optional)

## Getting Started

### Maven Dependency

```xml
<dependency>
    <groupId>com.berexia</groupId>
    <artifactId>business-event-manager</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Configuration Properties

```yaml
berexia:
  event:
    manager:
      # Enable/disable specific consumers
      consumers:
        ship-arrival:
          enabled: true
        dap-request:
          enabled: true
      
      # RabbitMQ configuration  
      rabbit:
        ship-arrival:
          queue-name: ship-arrival-queue
          exchange-name: ship-arrival-exchange
          routing-key: ship.arrival.event
        dap-request:
          queue-name: dap-request-queue
          exchange-name: dap-request-exchange
          routing-key: dap.request.event
          
      # Blockchain integration (optional)
      blockchain:
        enabled: true
        rpc-url: ${BLOCKCHAIN_RPC_URL}
        ship-arrival-contract: ${SHIP_ARRIVAL_CONTRACT_ADDRESS}
        dap-contract: ${DAP_CONTRACT_ADDRESS}
        private-key: ${BLOCKCHAIN_PRIVATE_KEY}
```

### Environment Configuration

For blockchain integration, it's recommended to use environment variables for sensitive information. Create a `.env` file in your project root:

```properties
# Blockchain Configuration
BLOCKCHAIN_RPC_URL=http://157.173.119.195:8545
BLOCKCHAIN_PRIVATE_KEY=your-private-key-here
SHIP_ARRIVAL_CONTRACT_ADDRESS=0x...
DAP_CONTRACT_ADDRESS=0x...
```

Then, import it in your application.yml:

```yaml
spring:
  config:
    import:
      - optional:file:.env[.properties]
```

## Event Types

### Ship Arrival

```json
{
  "shipId": "SHIP-1234",
  "shipName": "Sample Ship",
  "callId": "CALL-5678",
  "portId": "PORT-9012",
  "portName": "Sample Port",
  "operatorName": "Sample Operator",
  "status": "ARRIVED",
  "expectedArrivalTime": "2023-03-25T14:30:00Z",
  "actualArrivalTime": "2023-03-25T15:00:00Z"
}
```

### Docking Request (DAP)

```json
{
  "noticeNumber": 123456,
  "callNumber": "CALL-5678",
  "portName": "Sample Port",
  "portCode": "SP01",
  "shipName": "Sample Ship",
  "operatorName": "Sample Operator",
  "status": "PENDING",
  "requestDate": "2023-03-24T10:00:00Z"
}
```

## Database Schema

The library creates two tables:

1. `ship_arrivals`
2. `dap_requests`

## Blockchain Integration

The library can optionally record events on a blockchain for immutability and auditability. Two smart contracts are used:

1. **ShipArrivalContract**: Records ship arrival events
2. **DAPContract**: Records DAP (docking request) events

To set up blockchain integration:

1. Deploy the smart contracts using the deployment scripts in `blockchain/berexia-chain`
2. Update the `.env` file with the deployed contract addresses
3. Enable blockchain integration in your application configuration

For details on the smart contracts and deployment, see [Blockchain Contracts Documentation](/blockchain/berexia-chain/README.md).

## Requirements

- Java 17 or higher
- Spring Boot 3.1+
- RabbitMQ 3.8+
- A supported database (MySQL, PostgreSQL, H2)
- (Optional) Access to an Ethereum-compatible blockchain node 