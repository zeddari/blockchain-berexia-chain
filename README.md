# Berexia Trade - Event Management Solution

## Overview

This project implements a robust event management solution for handling ship arrivals and DAP (Demande d'Autorisation Préalable) requests within the TradeChain ecosystem. It consists of two main components:

1. **Business Event Manager Library** (`libs/business-event-manager`): A reusable library that handles the consumption, processing, and persistence of business events.

2. **Sample Event Consumer Application** (`apps/sample-event-consumer`): A Spring Boot application that demonstrates how to use the Business Event Manager library.

## Features

- RabbitMQ message consumption for ship arrival and DAP request events
- Automatic persistence of event data to a relational database
- Support for conditional enablement of event consumers
- Error handling and recovery
- Sample application demonstrating proper usage patterns

## Project Structure

```
berexia-trade/
├── apps/
│   └── sample-event-consumer/      # Demo application
│       ├── src/                    # Source code
│       ├── pom.xml                 # Maven configuration
│       └── README.md               # Application documentation
├── libs/
│   └── business-event-manager/     # Core library
│       ├── src/                    # Source code
│       ├── pom.xml                 # Maven configuration
│       └── README.md               # Library documentation
└── README.md                       # This file
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- RabbitMQ 3.8+
- MySQL/PostgreSQL/H2 database

### Building the Project

To build the entire project:

```bash
mvn clean install
```

### Running the Sample Application

See the [Sample Event Consumer README](apps/sample-event-consumer/README.md) for detailed instructions.

## Business Event Manager Library

The Business Event Manager library is designed to be easily integrated into any Spring Boot application that needs to process ship arrival or DAP request events.

For detailed documentation, see the [Business Event Manager README](libs/business-event-manager/README.md).

## Integration with Legacy DAP Service

The Business Event Manager is designed to replace the DAP message listener in the legacy DAP service. It provides a more structured and maintainable approach to handling DAP events with the following advantages:

- Standardized event processing pipeline
- Automatic persistence of event data
- Improved error handling and recovery
- Configurable event consumption
- Clean separation of concerns

## License

This project is proprietary and confidential. All rights reserved. 
