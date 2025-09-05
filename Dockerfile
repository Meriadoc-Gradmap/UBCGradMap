FROM gradle:8.11-jdk17 AS build

ENV PORT=5173
EXPOSE 5173 8080 80

WORKDIR /gradmap

COPY . .

WORKDIR /gradmap 

RUN ./gradlew clean

ENTRYPOINT ["./gradlew", "clean", "run"]
