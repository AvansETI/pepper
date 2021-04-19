FROM maven:3.8.1-adoptopenjdk-16-openj9
WORKDIR /app
COPY . .
RUN mvn clean package
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "target/app.jar"]
