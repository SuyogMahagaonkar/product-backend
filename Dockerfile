# ---------- Stage 1: Build ----------
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the SSL certificate required for Aiven
COPY ca.pem /app/ca.pem

# Expose Spring Boot default port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
