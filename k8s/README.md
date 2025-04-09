# Kubernetes Deployment for TradeChain Platform

This directory contains Kubernetes configuration files for deploying the TradeChain platform on a Kubernetes cluster. The deployment is also compatible with Minikube for local development and testing.

## Components

The deployment consists of the following components:

1. **RabbitMQ** - Message broker for event-driven communication
2. **MySQL** - Database for persistent storage
3. **Orchestrator Service** - The workflow engine for the TradeChain platform

## Prerequisites

- Kubernetes cluster (or Minikube for local development)
- kubectl command-line tool
- Docker (if building custom images)

## Deployment Instructions

### 1. Setting up Minikube (for local development)

```bash
# Start Minikube
minikube start

# Enable the Minikube Docker daemon
eval $(minikube docker-env)
```

### 2. Building the Docker image for the orchestrator service

```bash
# Navigate to the orchestrator service directory
cd apps/orchestrator-service

# Build the Docker image
docker build -t berexia/orchestrator-service:latest .
```

### 3. Deploying the application

```bash
# Create the RabbitMQ resources
kubectl apply -f k8s/rabbitmq/

# Create the MySQL resources
kubectl apply -f k8s/mysql/

# Create the orchestrator service resources
kubectl apply -f k8s/orchestrator-service/
```

### 4. Switching between Development and Production Profiles

The orchestrator service is configured with two Spring profiles:

- **dev**: For local development with Minikube
- **prod**: For production deployment

To switch between profiles, edit the `k8s/orchestrator-service/env-configmap.yaml` file:

```yaml
data:
  active.profile: "dev"  # Change this to "prod" for production
```

Then apply the changes:

```bash
kubectl apply -f k8s/orchestrator-service/env-configmap.yaml
kubectl rollout restart deployment orchestrator-service
```

#### Production Configuration

When using the production profile, you may want to update the external service configurations:

```bash
# Edit the production configuration
kubectl edit configmap orchestrator-prod-config

# Apply the changes
kubectl rollout restart deployment orchestrator-service
```

### 5. Verifying the deployment

```bash
# Check the status of the pods
kubectl get pods

# Check the status of the services
kubectl get services
```

### 6. Accessing the services

#### RabbitMQ Management Console

```bash
# Port forward the RabbitMQ management console
kubectl port-forward service/rabbitmq 15672:15672
```

You can now access the RabbitMQ management console at http://localhost:15672 with the credentials:
- Username: guest
- Password: guest

#### Orchestrator Service API

```bash
# Port forward the orchestrator service
kubectl port-forward service/orchestrator-service 8080:8080
```

You can now access the orchestrator service API at http://localhost:8080

## Cleaning up

```bash
# Delete all resources
kubectl delete -f k8s/orchestrator-service/
kubectl delete -f k8s/mysql/
kubectl delete -f k8s/rabbitmq/
```

## Configuration

### Environment Variables

The orchestrator service uses the following environment variables:

- `ACTIVE_PROFILE` - The active Spring profile (dev or prod)
- `MYSQL_USER` - MySQL username
- `MYSQL_PASSWORD` - MySQL password
- `RABBITMQ_USER` - RabbitMQ username
- `RABBITMQ_PASSWORD` - RabbitMQ password
- `RABBITMQ_HOST` - RabbitMQ host (production profile only)
- `RABBITMQ_PORT` - RabbitMQ port (production profile only)
- `SHIP_MANAGEMENT_URL` - Ship Management Service URL (production profile only)

These environment variables are set in the Kubernetes deployment files using Kubernetes secrets and ConfigMaps.

### Profile-Specific Configurations

#### Development Profile (dev)

The development profile is configured with:

- Database: In-cluster MySQL
- Blockchain RPC: http://157.173.119.195:8545 and ws://157.173.119.195:8546
- RabbitMQ: In-cluster instance
- Microservices: In-cluster services

#### Production Profile (prod)

The production profile is configured with:

- Database: In-cluster MySQL (can be changed to external)
- Blockchain RPC: http://157.173.119.195:8545 and ws://157.173.119.195:8546
- RabbitMQ: Configurable via environment variables
- Microservices: Configurable via environment variables

## Notes for Production Deployment

For a production deployment, consider the following:

1. Use a proper Kubernetes Ingress controller for exposing services
2. Use a managed RabbitMQ and MySQL service instead of running them in the cluster
3. Set up proper resource limits and requests
4. Use Kubernetes StatefulSets for stateful components
5. Implement proper backup and disaster recovery procedures
6. Use Kubernetes Horizontal Pod Autoscaler for scaling
7. Use a proper CI/CD pipeline for building and deploying the application 