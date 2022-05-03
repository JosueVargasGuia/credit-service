FROM openjdk:11
EXPOSE  8084
ADD     ./target/*.jar credit-service.jar
ENTRYPOINT ["java","-jar","credit-service.jar"]