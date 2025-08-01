# Stage 1: Build the application
FROM gradle:8.14-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN gradle bootJar --no-daemon

# Stage 2: Run the application
FROM gcr.io/distroless/java21-debian12:debug-nonroot

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --retries=5 CMD wget --spider --quiet http://localhost:8080/api/actuator/health

# Command to run the application
USER nonroot
CMD ["app.jar"]