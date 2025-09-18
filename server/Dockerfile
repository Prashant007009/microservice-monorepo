# Stage 1: Build the application with Maven
FROM maven:3.8.1-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Build the application (JAR file) with Maven
RUN mvn clean package -DskipTests

# Stage 2: Use a smaller runtime image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage into the container
COPY --from=builder /app/target/*.jar app.jar

# Expose port (same as in your application.properties config)
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]