FROM eclipse-temurin:21-jre

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["/bin/sh", "-c", "exec java -jar app.jar"]