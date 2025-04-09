FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent pom.xml file
COPY ../pom.xml ../pom.xml

# Copy the orchestrator service pom.xml file
COPY pom.xml .

# Copy the libs directory
COPY ../libs ../libs

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"] 