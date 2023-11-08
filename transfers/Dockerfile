FROM eclipse-temurin:21-jre-alpine

ADD ./build/libs/transfers-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/transfers-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080 8081