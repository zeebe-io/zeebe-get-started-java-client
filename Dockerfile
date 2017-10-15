FROM openjdk:8-jre-alpine

COPY target/zeebe-get-started-java-client-0.1.0-jar-with-dependencies.jar /usr/src/zeebe-worker.jar
ENTRYPOINT ["java", "-jar", "/usr/src/zeebe-worker.jar"]
