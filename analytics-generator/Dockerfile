# Build from repo root: docker build -f analytics-generator/Dockerfile .
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml .
COPY analytics-server/pom.xml analytics-server/
COPY analytics-generator/pom.xml analytics-generator/
COPY analytics-generator analytics-generator

RUN mvn -q -B -pl analytics-generator clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/analytics-generator/target/analytics-generator-*.jar generator.jar
ENV INGEST_URL=http://analytics-server:9000
ENV INTERVAL_MS=500
ENTRYPOINT ["java", "-jar", "generator.jar"]
