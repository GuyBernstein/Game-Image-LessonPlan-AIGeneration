FROM --platform=linux/amd64 openjdk:11
COPY target/lesson_generator*.jar /usr/src/lesson_generator.jar
COPY src/main/resources/application.properties /opt/conf/application.properties
CMD ["java", "-jar", "/usr/src/lesson_generator.jar", "--spring.config.location=file:/opt/conf/application.properties"]

