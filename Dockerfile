FROM maven:3.8.3-openjdk-17

COPY . /opt/liferay/microservice
WORKDIR /opt/liferay/microservice

RUN ["mvn", "clean", "install"]

EXPOSE 9090

CMD ["java", "-jar", "build/microservice-0.0.1-SNAPSHOT.jar"]