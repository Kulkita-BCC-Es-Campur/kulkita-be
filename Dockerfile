# Multi-stage build untuk optimasi ukuran image
FROM openjdk:21-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper dan dependency files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (untuk caching layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build aplikasi
RUN ./mvnw clean package -DskipTests

# Production stage
FROM openjdk:21-jre-slim

# Install curl untuk health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app user untuk security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR file dari builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy Firebase service account file jika ada
COPY --chown=appuser:appuser src/main/resources/firebase-service-account.json* /app/

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables dengan default values
ENV SPRING_PROFILES_ACTIVE=docker
ENV JVM_OPTS="-Xmx512m -Xms256m"

# Run aplikasi
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]