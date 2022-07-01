FROM openjdk:17-slim
EXPOSE 8080
VOLUME /app
ADD target/cashx-1.0.0-SNAPSHOT-fat.jar app.jar
WORKDIR app
CMD ["java", "-jar", "/app.jar"]
