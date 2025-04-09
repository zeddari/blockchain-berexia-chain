# Orchestrator Service

## Overview
The Orchestrator Service is a workflow-based orchestration engine for the TradeChain platform. It manages business processes and coordinates activities across various services in the system by directly calling their APIs.

## Features
- Flowable-based workflow engine
- Internal API for workflow management
- Direct integration with microservices (ship-management-service, etc.)
- Ship arrival process workflow orchestration
- Manifest Workflow for coordinating DAP and ship arrival activities

## Technology Stack
- Java 17
- Spring Boot 3.2.2
- Flowable BPM 6.8.0
- Spring Data JPA
- Spring RestTemplate for direct service calls
- MySQL Database
- RabbitMQ for event-based communication

## Architecture
The orchestrator service acts as the central coordinator for business processes in the TradeChain ecosystem:
1. It receives workflow triggers from various sources
2. It orchestrates the workflow steps using Flowable BPM
3. It directly calls other microservices' APIs for business operations
4. It tracks the workflow states and maintains process history
5. It listens for events from RabbitMQ to progress workflows

## Workflows
### Ship Arrival Process
This workflow handles the processing of ship arrival notices:
1. Validation of arrival notice data
2. Local persistence for auditing and tracking
3. Notification to the Ship Management Service via direct API call
4. Error handling for invalid notices or failed service calls

### Manifest Workflow
This workflow coordinates the processing of ship arrivals, DAP requests, and blockchain recording:
1. Calls Ship Arrival API to initiate ship arrival
2. Waits for Ship Arrival events from RabbitMQ
3. Records the Ship Arrival information on the blockchain
4. Calls DAP API to initiate docking request
5. Waits for DAP events from RabbitMQ
6. Records the DAP information on the blockchain

## Profiles
The application supports two profiles for different environments:

### Development Profile (dev)
The development profile is configured for local development:
- Database: MySQL on localhost
- RabbitMQ: Local RabbitMQ instance
- Services: Local microservices

To run the application with the development profile:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Production Profile (prod)
The production profile is configured for deployment:
- Database: MySQL configurable via environment variables
- RabbitMQ: Configurable via environment variables
- Services: Configurable via environment variables

To run the application with the production profile:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## Blockchain Integration
The application integrates with the Ethereum blockchain using the following endpoints:
- JSON-RPC HTTP: http://157.173.119.195:8545
- JSON-RPC WebSocket: ws://157.173.119.195:8546

### Smart Contract Deployment
The service includes built-in tools for deploying smart contracts to the Ethereum blockchain:

```bash
# Deploy contracts using the provided script
./deploy-contracts.sh [ethereum_node_url] [private_key]

# Or using Maven directly
mvn clean package -Pdeploy-contracts \
    -Dethereum.node.url="http://ethereum-node:8545" \
    -Dethereum.wallet.privateKey="YOUR_PRIVATE_KEY"
```

For detailed instructions, see the [CONTRACT-DEPLOYMENT.md](CONTRACT-DEPLOYMENT.md) document.

## Internal API Endpoints
- `POST /api/workflows/manifest/start`: Start a new manifest workflow
- `GET /api/workflows/manifest/{instanceId}`: Get the status of a manifest workflow

## Service Dependencies
The orchestrator service makes direct API calls to the following services:
- **Ship Management Service**: For handling ship arrival and DAP notices
  - Endpoint: `POST /api/ship-arrivals` (Ship Arrival)
  - Endpoint: `POST /api/docking-requests` (DAP)
  - Host: ship-management-service:8081 (in cluster) or localhost:8081 (local development)

## Building and Running
This service is built using Maven. Use the following commands:

```bash
mvn clean install
mvn spring-boot:run
```

## Kubernetes Deployment
For deployment to Kubernetes, see the `k8s/` directory in the root of the project. The deployment supports switching between development and production profiles.

## Configuration
The service is configured to connect to:
- MySQL database for workflow state storage
- Other microservices via direct HTTP calls
- RabbitMQ for event-based communication
- Ethereum blockchain via JSON-RPC

Environment-specific configuration is managed through Spring profiles and Kubernetes ConfigMaps.

## Dependencies
See the `pom.xml` file for a complete list of dependencies. The service depends on the Business Event Manager library. 