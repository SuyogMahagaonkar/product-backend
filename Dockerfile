# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set a working directory
WORKDIR /app

# Copy the built jar file into the image
COPY target/*.jar app.jar

# Expose port (Spring Boot default is 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
