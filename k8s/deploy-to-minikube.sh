#!/bin/bash

# Exit on error
set -e

echo "Deploying TradeChain platform to Minikube..."

# Check if Minikube is running
if ! minikube status &>/dev/null; then
  echo "Starting Minikube..."
  minikube start
fi

# Enable the Minikube Docker daemon
echo "Using Minikube Docker daemon..."
eval $(minikube docker-env)

# Build the Docker image for the orchestrator service
echo "Building orchestrator service Docker image..."
cd ../apps/orchestrator-service
docker build -t berexia/orchestrator-service:latest .
cd ../../k8s

# Create the RabbitMQ resources
echo "Deploying RabbitMQ..."
kubectl apply -f rabbitmq/

# Create the MySQL resources
echo "Deploying MySQL..."
kubectl apply -f mysql/

# Create the orchestrator service resources
echo "Deploying orchestrator service..."
kubectl apply -f orchestrator-service/

# Wait for all pods to be ready
echo "Waiting for pods to be ready..."
kubectl wait --for=condition=Ready --timeout=300s pod -l app=rabbitmq
kubectl wait --for=condition=Ready --timeout=300s pod -l app=mysql
kubectl wait --for=condition=Ready --timeout=300s pod -l app=orchestrator-service

echo "Deployment completed successfully!"
echo "Use the following commands to access the services:"
echo "  - RabbitMQ Management Console: kubectl port-forward service/rabbitmq 15672:15672"
echo "  - Orchestrator Service API: kubectl port-forward service/orchestrator-service 8080:8080" 