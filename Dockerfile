FROM openjdk:17
EXPOSE 9090
ADD target/trader-app.jar trader-app.jar
ENTRYPOINT ["java", "-jar", "/trader-app.jar"]