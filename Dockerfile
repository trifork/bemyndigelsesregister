FROM registry.nspop.dk/platform/nsp:latest AS nspbuilder

FROM quay.io/wildfly/wildfly:latest

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/
RUN mkdir -p /opt/jboss/wildfly/modules/com/mysql/main/
COPY --from=nspbuilder /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/* /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/
COPY --from=nspbuilder /pack/wildfly8/modules/com/mysql/main/* /opt/jboss/wildfly/modules/com/mysql/main/

# RUN /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="module add --name=com.mysql --resources=/tmp/mysql-connector-java-${MYSQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api" 
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command=/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql.driver,driver-class-name=com.mysql.jdbc.Driver)

# Copy the war file to the deployment directory
ADD service/target/bem.war /opt/jboss/wildfly/standalone/deployments/

#RUN echo "#Skip nothing" > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/security.skip

#RUN echo '.*/(health|dksconfig|u)$' > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
