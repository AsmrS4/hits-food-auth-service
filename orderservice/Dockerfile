FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY common-module ./common-module
COPY orderservice ./orderservice

RUN cd common-module && mvn clean install -DskipTests
RUN cd orderservice && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/orderservice/target/orderservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8096
ENTRYPOINT ["java", "-jar", "app.jar"]