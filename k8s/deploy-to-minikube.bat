@echo off
echo Deploying TradeChain platform to Minikube...

REM Check if Minikube is running
minikube status
if %ERRORLEVEL% NEQ 0 (
  echo Starting Minikube...
  minikube start
)

REM Enable the Minikube Docker daemon
echo Using Minikube Docker daemon...
@FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO @%%i

REM Build the Docker image for the orchestrator service
echo Building orchestrator service Docker image...
cd ..\apps\orchestrator-service
docker build -t berexia/orchestrator-service:latest .
cd ..\..\k8s

REM Create the RabbitMQ resources
echo Deploying RabbitMQ...
kubectl apply -f rabbitmq/

REM Create the MySQL resources
echo Deploying MySQL...
kubectl apply -f mysql/

REM Create the orchestrator service resources
echo Deploying orchestrator service...
kubectl apply -f orchestrator-service/

REM Wait for all pods to be ready
echo Waiting for pods to be ready...
echo This may take several minutes...

echo Deployment initiated!
echo Use the following commands to check deployment status:
echo   kubectl get pods
echo   kubectl get services
echo.
echo Once all pods are ready, use these commands to access the services:
echo   - RabbitMQ Management Console: kubectl port-forward service/rabbitmq 15672:15672
echo   - Orchestrator Service API: kubectl port-forward service/orchestrator-service 8080:8080 