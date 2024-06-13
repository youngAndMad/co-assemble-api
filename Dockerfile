#FROM gradle:7.6.0-jdk17 AS build
#WORKDIR /app
#COPY --chown=gradle:gradle . /app
#RUN gradle build --no-daemon
#
#FROM openjdk:17.0.2-slim AS builder
#WORKDIR /app
#COPY --from=build /app/build/libs/*.jar /app/co-assemble-api.jar
#RUN java -Djarmode=layertools -jar co-assemble-api.jar extract
#
#FROM openjdk:17.0.2-slim
#WORKDIR /app
#COPY --from=builder /app/dependencies/ ./
#COPY --from=builder /app/spring-boot-loader/ ./
#COPY --from=builder /app/snapshot-dependencies/ ./
#COPY --from=builder /app/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

FROM openjdk:17.0.2-slim
WORKDIR /app
COPY build/libs/*.jar /app/co-assemble-api.jar
ENTRYPOINT ["java", "-jar", "co-assemble-api.jar"]