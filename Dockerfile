ARG JAR_FILE=service/target/bem-exec.jar
ARG PROPERTIES_FILE=etc/application.properties
ARG LOG4J2_FILE=etc/log4j2.xml
ARG KAFKA_FILE=etc/bem-kafka-producer.properties
ARG IDWS_RESPONSE_SIGNING_CERT=etc/NSP_Test_Service_Consumer_sds.p12
ARG JAVA_VERSION=21
ARG BASE_IMAGE=eclipse-temurin:${JAVA_VERSION}-jre

FROM ${BASE_IMAGE}
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

ARG IDWS_RESPONSE_SIGNING_CERT
COPY ${IDWS_RESPONSE_SIGNING_CERT} NSP_Test_Service_Consumer_sds.p12

CMD ["java", "-Dlogging.config=/log4j2.xml", "-jar", "application.jar"]
