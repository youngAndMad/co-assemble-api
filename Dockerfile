FROM gradle:7.6.0-jdk17 AS builder

WORKDIR /home/gradle/app

COPY build.gradle.kts settings.gradle.kts /home/gradle/app/
COPY src /home/gradle/app/src

RUN gradle build --no-daemon

FROM openjdk:17-slim

WORKDIR /app

COPY --from=builder /home/gradle/app/build/libs/*.jar /app/co-assemble-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "co-assemble-api.jar"]
