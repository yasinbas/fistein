# Multi-stage build for Spring Boot application
FROM openjdk:21-jdk-slim as builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Production image
FROM openjdk:21-jre-slim

WORKDIR /app

# Create non-root user
RUN groupadd -r fistein && useradd -r -g fistein fistein

# Copy jar file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p logs && chown -R fistein:fistein /app

# Switch to non-root user
USER fistein

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]