# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copy only the POM first to cache dependencies (Senior optimization trick!)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy the source code and then build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the minimal runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
# Extract the compiled JAR from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose the API port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]