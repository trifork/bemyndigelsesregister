FROM tomcat:7-jre8-slim

ENV TZ Europe/Copenhagen

ENV HTTP_PORT "8080"
ENV AJP_PORT "8009"
ENV SHUTDOWN_PORT "8005"
ENV CATALINA_OPTS "-Xms768m -Xmx768m -XX:MaxMetaspaceSize=456m -XX:CompressedClassSpaceSize=64m  -Dhttp.port=${HTTP_PORT} -Dajp.port=${AJP_PORT} -Dshutdown.port=${SHUTDOWN_PORT} -Ddgws.configuration.dir=$CATALINA_HOME/dgws-conf -Deventbox.configuration.dir=$CATALINA_HOME/eventbox-conf -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -javaagent:/pack/jolokia/jolokia-jvm-1.6.1-agent.jar=port=8888,config=/usr/local/tomcat/conf/agent.properties"

# FROM TEST1: 
# /pack/jdk/bin/java -Djava.util.logging.config.file=/pack/tomcat/conf/logging.properties -Xms768m -Xmx768m -XX:MaxMetaspaceSize=456m -XX:CompressedClassSpaceSize=64m -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Dbemyndigelse.home=/pack/tomcat/conf/bemyndigelse.properties -Dlog4j.configuration=file:///pack/tomcat/conf/log4j.xml -Djava.endorsed.dirs=/pack/tomcat/endorsed -classpath /pack/tomcat/bin/bootstrap.jar:/pack/tomcat/bin/tomcat-juli.jar -Dcatalina.base=/pack/tomcat -Dcatalina.home=/pack/tomcat -Djava.io.tmpdir=/pack/tomcat/temp org.apache.catalina.startup.Bootstrap start

WORKDIR /usr/local/tomcat

RUN rm -rf webapps/ROOT
RUN rm -rf webapps/docs
RUN rm -rf webapps/examples

COPY ./bemyndigelse-integration-test/target/cargo/configurations/tomcat7x/webapps/ROOT.war ./webapps/
RUN apt-get update
RUN apt-get install wget
RUN wget http://search.maven.org/remotecontent?filepath=org/jolokia/jolokia-jvm/1.6.1/jolokia-jvm-1.6.1-agent.jar

# Configure tomcat
COPY ./bemyndigelse-shared/src/main/resources/bemyndigelse.properties ./conf/
COPY ./bemyndigelse-shared/src/main/resources/log4j.xml ./conf/

EXPOSE ${HTTP_PORT}

ENTRYPOINT ["catalina.sh", "run"]
