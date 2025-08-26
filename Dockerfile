FROM gradle:8.11-jdk21-jammy  AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean bootJar -Pvaadin.productionMode --no-daemon

FROM eclipse-temurin:21.0.3_9-jdk

RUN mkdir /app

COPY /opentelemetry/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar


COPY --from=build /home/gradle/src/build/libs/*.jar /app/main.jar

COPY --from=build /home/gradle/src/src/main/resources /app/resources

ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/main.jar"]
