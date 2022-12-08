FROM registry.nspop.dk/platform/nsp:latest AS nspbuilder

FROM quay.io/wildfly/wildfly:latest

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/
COPY --from=nspbuilder /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/* /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/

# Copy the war file to the deployment directory
ADD service/target/bem.war /opt/jboss/wildfly/standalone/deployments/

#RUN echo "#Skip nothing" > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/security.skip

#RUN echo '.*/(health|dksconfig|u)$' > /opt/jboss/wildfly/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
