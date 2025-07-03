# Use OpenJDK 21 base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Copy source code
COPY src ./src

# Download dependencies
RUN ./mvnw dependency:go-offline

# Expose port
EXPOSE 8080

# Run the application in development mode
CMD ["./mvnw", "quarkus:dev", "-Dquarkus.http.host=0.0.0.0", "-Dquarkus.profile=docker"]
