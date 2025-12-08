FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY config-service ./config-service
RUN cd config-service && mvn clean package -U -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/config-service/target/config-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9297
ENTRYPOINT ["java", "-jar", "app.jar"]