FROM gradle:jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

LABEL org.name="shenlinzheng"
#
# Package stage
#
FROM eclipse-temurin:21-jdk-jammy
EXPOSE 80
COPY --from=build /home/gradle/src/build/libs/game-archive-manager-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]