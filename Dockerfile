FROM maven:3.8.6-amazoncorretto-17 as build

WORKDIR /app

COPY . /app

RUN mvn clean package

FROM openjdk:17.0.2-jdk-slim-buster

EXPOSE 8080

COPY --from=build /app/target/trellei-backend-0.0.1-SNAPSHOT.jar /app/trellei-backend-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/trellei-backend-0.0.1-SNAPSHOT.jar"]