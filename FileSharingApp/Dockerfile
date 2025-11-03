# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml

# Download dependencies first (this helps with Docker cache)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the Spring Boot application (fat jar)
RUN ./mvnw package -DskipTests

# Expose the port your app runs on (default 8080)
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "target/FileSharingApp-0.0.1-SNAPSHOT.jar"]
