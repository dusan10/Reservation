FROM openjdk:17
ADD target/reservation-0.0.1-SNAPSHOT.jar /reservation-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "reservation-0.0.1-SNAPSHOT.jar"]