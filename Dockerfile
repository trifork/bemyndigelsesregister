ARG JAR_FILE=service/target/bem-exec.jar
ARG PROPERTIES_FILE=etc/application.properties
ARG LOG4J2_FILE=etc/log4j2.xml
ARG KAFKA_FILE=etc/bem-kafka-producer.properties

FROM amazoncorretto:11.0.14-alpine
# Set Timezone
ENV TZ Europe/Copenhagen

ARG JAR_FILE
COPY ${JAR_FILE} application.jar

ARG PROPERTIES_FILE
COPY ${PROPERTIES_FILE} application.properties

ARG LOG4J2_FILE
COPY ${LOG4J2_FILE} log4j2.xml

ARG KAFKA_FILE
COPY ${KAFKA_FILE} bem-kafka-producer.properties

CMD ["java", "-Dlogging.config=/log4j2.xml", "-jar", "application.jar"]
