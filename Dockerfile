FROM registry.nspop.dk/platform/nsp:latest AS nspbuilder

FROM quay.io/wildfly/wildfly:latest

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp
COPY --from=nspbuilder --chown=1000 /pack/wildfly8/modules/system/layers/base/dk/sds/nsp /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp
COPY --from=nspbuilder --chown=1000 /pack/wildfly8/modules/system/layers/base/org/apache/james /opt/jboss/wildfly/modules/system/layers/base/org/apache/james

#Fix kafka driver
RUN sed sed -i.bak s/org.apache.kafka.clients/org.apache.kafka.client/g /opt/jboss/wildfly/modules/system/layers/base/dk/nsp/minlog/producer/main/module.xml && rm /opt/jboss/wildfly/modules/system/layers/base/dk/nsp/minlog/producer/main/module.xml.bak
RUN sed sed -i.bak s/org.apache.kafka.clients/org.apache.kafka.client/g /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/kafka/clients/main/module.xml && rm /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/kafka/clients/main/module.xml.bak
RUN sed sed -i.bak s/org.apache.kafka.clients/org.apache.kafka.client/g /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/kafka/provider/main/module.xml && rm /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/kafka/provider/main/module.xml.bak

ENV MYSQL_VERSION 6.0.6

RUN echo "=> Starting WildFly server to add mysql jdbc connector" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading MySQL driver" && \
      curl --location --output /tmp/mysql-connector-java-${MYSQL_VERSION}.jar --url http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar && \
    echo "=> Adding mysql module" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=com.mysql --resources=/tmp/mysql-connector-java-${MYSQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api"

RUN echo "=> Starting WildFly server to add mysql module" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Adding mysql datasource" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.jdbc.Driver)"
     #  /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)"

#RUN echo "=> Starting WildFly server to install resteasy-spring" && \
#      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
#    echo "=> Waiting for the server to boot" && \
#      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
#    echo "=> Downloading Resteasy-spring driver" && \
#      curl --location --output /tmp/resteasy-spring.jar --url https://search.maven.org/remotecontent?filepath=org/jboss/resteasy/spring/resteasy-spring/3.0.0.Final/resteasy-spring-3.0.0.Final.jar && \
#    echo "=> Adding resteasy-spring module" && \
#      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=org.jboss.resteasy.resteasy-spring --resources=/tmp/resteasy-spring.jar"

RUN echo "=> Starting WildFly server to install apache commons pool" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading apache commons pool" && \
      curl --location --output /tmp/apache_commons_pool.jar --url https://search.maven.org/remotecontent?filepath=org/apache/directory/studio/org.apache.commons.pool/1.6/org.apache.commons.pool-1.6.jar && \
    echo "=> Adding apache ommons pool module" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=org.apache.commons.pool --resources=/tmp/apache_commons_pool.jar"

RUN echo "=> Starting WildFly server" && \
      bash -c '/opt/jboss/wildfly/bin/standalone.sh &' && \
    echo "=> Waiting for the server to boot" && \
      bash -c 'until `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `/opt/jboss/wildfly/bin/jboss-cli.sh -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Registring global module" && \
      /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=ee:write-attribute(name="global-modules",value=[{"name" => "dk.sds.nsp.accesshandler","slot" => "main","services" => "true","meta-inf"="true"},{"name" => "dk.sds.nsp.kafka.provider","slot" => "main","services" => "true","meta-inf"="true"}])"

RUN mkdir -p /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main
COPY --chown=1000 /compose/configuration/bemyndigelse.properties /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/bemyndigelse.properties
COPY --chown=1000 /compose/configuration/log4j-bem.xml /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/log4j-bem.xml
COPY --chown=1000 /compose/configuration/FMK-KRS-TEST.jks /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/FMK-KRS-TEST.jks
COPY --chown=1000 /etc/wildfly/modules/dk/bemyndigelsesregister/bem/main/module.xml /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/module.xml
#COPY /compose/configuration/log4j-nspslalog.properties /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/log4j-nspslalog.properties
#COPY /compose/configuration/nspslalog-bem.properties /opt/jboss/wildfly/modules/dk/bemyndigelsesregister/bem/main/nspslalog-bem.properties

# Copy the war file to the deployment directory
COPY --chown=1000 service/target/bem.war /opt/jboss/wildfly/standalone/deployments/

RUN echo "#Skip nothing" > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/security.skip

RUN echo '.*/(health|dksconfig|u)$' > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
