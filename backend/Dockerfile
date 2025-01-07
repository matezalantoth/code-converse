FROM openjdk:17-jdk-slim

RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /home/spring

ARG JAR_FILE=target/*.jar

COPY target/*.jar /app.jar

EXPOSE 8080

RUN chmod +x /app.jar

USER spring:spring

ENTRYPOINT ["java", "-jar", "/app.jar"]