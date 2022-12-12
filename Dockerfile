ARG BASE_TAG=latest
FROM registry.nspop.dk/platform/nsp:${BASE_TAG}

# Copy configuration files
COPY etc/wildfly /pack/wildfly8/

# Copy the war file to the deployment directory
COPY service/target/bem.war /pack/wildfly8/standalone/deployments/

RUN echo "#Skip nothing" > /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/security.skip

RUN echo '.*/(health|dksconfig|u)$' > /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
#RUN echo '.*' > /pack/wildfly8/modules/system/layers/base/dk/sds/nsp/accesshandler/main/handler.skip
