FROM openjdk:17-jdk-slim

# Create a new user 'spring' and group 'spring' without the -S flag
RUN groupadd -r spring && useradd -r -g spring spring

# Set the working directory to the user's home directory
WORKDIR /home/spring

# Set environment variable for the JAR file path
ARG JAR_FILE=target/*.jar
ENV DOCKER_API_KEY=${API_KEY}
# Copy the JAR file into the container
COPY target/*.jar /app.jar

# Expose the port
EXPOSE 8080

# Ensure the JAR file has execute permissions
RUN chmod +x /app.jar

# Switch to 'spring' user
USER spring:spring

# Run the app as the 'spring' user
ENTRYPOINT ["java", "-jar", "/app.jar"]