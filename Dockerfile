FROM eclipse-temurin:21-jre

COPY build/libs/*SNAPSHOT.jar app.jar


ENTRYPOINT ["/bin/bash", "-c", "echo $DB_HOST"]
ENTRYPOINT ["/bin/bash", "-c", "sleep 500"]