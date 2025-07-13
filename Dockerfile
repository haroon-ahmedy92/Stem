# Use a slim OpenJDK base image for running Java applications.
# 'temurin' is a popular choice for production-ready OpenJDK builds.
# 'jre-alpine' is very small, which helps keep your final image size down.
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container.
# All subsequent commands will be executed relative to this directory.
WORKDIR /app

# Copy the executable JAR file from your local 'target' directory
# into the '/app' directory within the Docker image.
# Make sure the JAR name matches exactly what you have (stemapplication-0.0.1-SNAPSHOT.jar).
COPY target/stemapplication-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that your Spring Boot application listens on.
# By default, Spring Boot applications run on port 8080.
# This makes the port available for mapping when you run the container.
EXPOSE 8000

# Define the command to run your Spring Boot application when the container starts.
# 'java -jar app.jar' is the standard way to execute a Spring Boot fat JAR.
# Using the 'exec' form (as an array) is generally preferred as it allows
# Docker to handle signals (like SIGTERM) properly for graceful shutdowns.
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add environment variables for Spring profiles or other configurations
# ENV SPRING_PROFILES_ACTIVE=production