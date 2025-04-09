# Sample Event Consumer

This application demonstrates how to use the Business Event Manager library to consume and process events from RabbitMQ.

## Overview

The Sample Event Consumer is a Spring Boot application that:

1. Publishes sample events to RabbitMQ (ship arrivals and DAP requests)
2. Consumes these events using the Business Event Manager library
3. Stores the processed events in an in-memory H2 database

## Features

- Automatic publishing of sample events on application startup
- Consumption and processing of events using the Business Event Manager
- In-memory database for storing event data
- H2 console for viewing the stored data

## Requirements

- Java 17+
- Maven 3.6+
- RabbitMQ 3.8+ (can be run using Docker)

## Running the Application

### With RabbitMQ in Docker

1. Start RabbitMQ in Docker:

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

2. Build and run the application:

```bash
mvn clean package
java -jar target/sample-event-consumer-0.0.1-SNAPSHOT.jar
```

### Using Maven

```bash
mvn spring-boot:run
```

## Accessing the H2 Console

The H2 console is available at: http://localhost:8081/h2-console

Connection details:
- JDBC URL: `jdbc:h2:mem:eventdb`
- Username: `sa`
- Password: `password`

## Available Endpoints

- Actuator endpoints: http://localhost:8081/actuator

## Configuration

The application uses the following configuration parameters, which can be modified in the `application.yaml` file:

- RabbitMQ connection details
- Database connection details
- Business Event Manager consumer settings

## Project Structure

- `SampleEventConsumerApplication.java`: Main application class with event publishing logic
- `application.yaml`: Application configuration
- `pom.xml`: Maven dependencies and build configuration 