FROM gradle:8.4.0-jdk17-focal AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM amazoncorretto:17.0.9

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/output.jar /app/output.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions","-jar","/app/output.jar"]