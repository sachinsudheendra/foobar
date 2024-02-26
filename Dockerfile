FROM maven:3.9.6-eclipse-temurin-21-alpine AS maven_deps
WORKDIR /app
COPY contract/pom.xml contract/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml
COPY kyc-service/pom.xml kyc-service/pom.xml
COPY pom.xml pom.xml
RUN mvn -B -e -C org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

FROM openjdk:21-slim-bookworm AS builder
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven protobuf-compiler
WORKDIR /app
COPY --from=maven_deps /root/.m2 /root/.m2
COPY --from=maven_deps /app /app
COPY . .
RUN mvn clean package -DskipTests=true

FROM openjdk:21-slim AS kycservice
WORKDIR /app
COPY --from=builder /app/kyc-service/target/kyc-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java",  "-jar", "kyc-service-0.0.1-SNAPSHOT.jar"]

FROM openjdk:21-slim AS customerservice
WORKDIR /app
COPY --from=builder /app/customer-service/target/customer-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java",  "-jar", "customer-service-0.0.1-SNAPSHOT.jar"]