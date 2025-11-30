FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY common-module ./common-module
COPY user-service ./user-service

RUN cd common-module && mvn clean install -DskipTests
RUN cd user-service && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/user-service/target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8910
ENTRYPOINT ["java", "-jar", "app.jar"]