FROM registry.nspop.dk/platform/nsp:latest AS nspbuilder

FROM quay.io/wildfly/wildfly:latest

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/
#RUN mkdir -p /opt/jboss/wildfly/modules/com/mysql/main/
COPY --from=nspbuilder /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/* /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/
#COPY --from=nspbuilder /pack/wildfly8/modules/com/mysql/main/* /opt/jboss/wildfly/modules/com/mysql/main/

ENV MYSQL_VERSION 6.0.6

RUN echo "=> Starting WildFly server" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading MySQL driver" && \
      curl --location --output /tmp/mysql-connector-java-${MYSQL_VERSION}.jar --url http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar && \
    echo "=> Adding mysql module" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=com.mysql --resources=/tmp/mysql-connector-java-${MYSQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api"

RUN echo "=> Starting WildFly server" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Adding mysql datasource" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.jdbc.Driver)"
     #  /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)"

RUN echo "=> Starting WildFly server" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading Resteasy-spring driver" && \
      curl --location --output /tmp/resteasy-spring.jar --url https://search.maven.org/artifact/org.jboss.resteasy.spring/resteasy-spring/3.0.0.Final/jar && \
    echo "=> Adding resteasy-spring module" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=org.jboss.resteasy.resteasy-spring --resources=/tmp/resteasy-spring.jar"

# Copy the war file to the deployment directory
ADD service/target/bem.war /opt/jboss/wildfly/standalone/deployments/

#RUN echo "#Skip nothing" > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/security.skip

#RUN echo '.*/(health|dksconfig|u)$' > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
