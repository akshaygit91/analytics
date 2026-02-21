# Build from repo root: docker build -f analytics-server/Dockerfile .
# Build stage: Maven + OpenJDK (eclipse-temurin is OpenJDK 21)
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml .
COPY analytics-server/pom.xml analytics-server/
COPY analytics-generator/pom.xml analytics-generator/
COPY analytics-server analytics-server

RUN mvn -q -B -pl analytics-server -am clean package -DskipTests

# Run stage: OpenJDK 21 JRE only
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/analytics-server/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
