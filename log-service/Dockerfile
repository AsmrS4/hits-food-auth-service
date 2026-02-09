FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY log-service ./log-service

RUN cd log-service && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/log-service/target/*.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "app.jar"]