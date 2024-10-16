# Use the more general openjdk:17-slim image which supports more platforms
FROM openjdk:17-slim

# Set the working directory in the container
WORKDIR /app

# Add the application's jar to the container
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Run the jar file when the container launches
ENTRYPOINT ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]