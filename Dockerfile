FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

FROM openjdk:17-alpine

CMD ["java", "-jar", "build/libs/companion-0.0.1-SNAPSHOT.jar"]
