FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY tools ./tools
COPY config.json ./

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]
