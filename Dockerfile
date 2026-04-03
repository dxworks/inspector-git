FROM eclipse-temurin:21-jre-alpine

RUN apk upgrade --no-cache

WORKDIR /app

COPY gitclient/target/iglog.jar iglog.jar
COPY gitclient/src/main/resources/instrument.yml instrument.yml

RUN mkdir -p results

ENTRYPOINT ["java", "-Xmx4g", "-jar", "iglog.jar"]
