FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY discovery-service ./discovery-service
RUN cd discovery-service && mvn clean package -U -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/discovery-service/target/discovery-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]