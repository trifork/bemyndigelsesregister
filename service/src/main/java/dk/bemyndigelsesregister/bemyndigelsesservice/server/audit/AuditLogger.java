package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.RequestContext;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.SystemService;
import dk.nsi.fmk.auditlog.client.AuditLogKafkaClient;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntry;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntryId;
import dk.nsi.fmk.moduleframework.data.ModuleFramework;
import dk.sds.nsp.security.SecurityContext;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Kafka auditlogger
 */
public class AuditLogger {
    private static final Logger log = Logger.getLogger(AuditLogger.class);

    @Inject
    SystemService systemService;

    private final boolean loggingEnabled;
    private final boolean useMock;

    private boolean configured = false;
    private AuditLogKafkaClient auditLogKafkaClient;

    public AuditLogger(boolean enabled, boolean useMock) {
        this.loggingEnabled = enabled;
        this.useMock = useMock;
        init();
    }

    public void close() {
        if (auditLogKafkaClient != null && auditLogKafkaClient.getAuditLogProducer() != null) {
            log.info("Closing AuditLogKafkaClient...");
            auditLogKafkaClient.getAuditLogProducer().close(30, TimeUnit.SECONDS);
        }
    }

    private void init() {
        if (loggingEnabled) {
            try {
                Properties props = new Properties();

                File configDir = new File(System.getProperty("catalina.base"), "conf");
                File configFile = new File(configDir, "bem-kafka-producer.properties");
                if (configFile.exists() && configFile.canRead()) {
                    log.info("Loading kafka properties from tomcat conf");
                    InputStream is = new FileInputStream(configFile);
                    props.load(is);
                } else {
                    InputStream is = AuditLogger.class.getResourceAsStream("/bem-kafka-producer.properties");
                    if (is != null) {
                        log.info("Loading kafka properties from resource");
                        props.load(is);
                    }
                }

                if (props.size() > 0) {
                    log.info("Kafka producer properties:");
                    for (String n : props.stringPropertyNames()) {
                        log.info("  " + n + "=" + props.getProperty(n));
                    }

                    auditLogKafkaClient = new AuditLogKafkaClient(props, useMock);
                    configured = true;
                    log.info("AuditLogKafkaClient configured, useMock=" + useMock);
                } else {
                    log.warn("Could not configure AuditLogKafkaClient - bem-kafka-producer.properties not found!");
                }
            } catch (Exception e) {
                log.error("Could not configure AuditLogKafkaClient - got exception", e);
            }
        } else {
            log.info("AuditLogging disabled.");
        }
    }

    public AuditLogEntryId log(String method, String delegateeCpr, SecurityContext securityContext) {
        if (configured) {
            log.debug("AuditLogging configured - starting logging.");

            AuditLogEntry.Builder entryBuilder = AuditLogEntry.newBuilder();

            String messageId = RequestContext.get().getMessageId();
            entryBuilder.setAccessType("Soap");
            entryBuilder.setServerSystemName("BEM");
            entryBuilder.setServiceVersion(systemService.getImplementationVersion());
            entryBuilder.setRequestId(messageId);
            entryBuilder.setMessageId(messageId);
            entryBuilder.setMethod(method);
            entryBuilder.setTimestamp(System.currentTimeMillis());

            Optional<SecurityContext.User> actingUser = securityContext.getActingUser();
            if (actingUser.isPresent()) {
                entryBuilder.setCpr(actingUser.get().getIdentifier());
                if (delegateeCpr != null) {
                    entryBuilder.setPersonCPR(delegateeCpr);
                }
                if (actingUser.get().getCredentials().isPresent() && actingUser.get().getCredentials().get().getUnverifiedRole().isPresent()) {
                    entryBuilder.setRole(actingUser.get().getCredentials().get().getUnverifiedRole().get());
                }
                if (actingUser.get().getCredentials().isPresent() && actingUser.get().getCredentials().get().getAuthorizationCode().isPresent()) {
                    entryBuilder.setAuthorizationNumber(actingUser.get().getCredentials().get().getAuthorizationCode().get());
                }

                StringBuilder b = new StringBuilder();
                if (actingUser.get().getGivenName().isPresent()) {
                    b.append(actingUser.get().getGivenName().get());
                }
                if (actingUser.get().getSurname().isPresent()) {
                    if (b.length() > 0) {
                        b.append(" ");
                    }
                    b.append(actingUser.get().getSurname().get());
                }
                if (b.length() > 0) {
                    entryBuilder.setUserName(b.toString());
                }
            }

            Optional<SecurityContext.Organisation> organisation = securityContext.getOrganisation();
            if (organisation.isPresent()) {
                entryBuilder.setCvr(organisation.get().getIdentifier());
                if (organisation.get().getName().isPresent()) {
                    entryBuilder.setOrganisationName(organisation.get().getName().get());
                }
            }

            Optional<SecurityContext.Client> client = securityContext.getClient();
            if (client.isPresent() && client.get().getName().isPresent()) {
                entryBuilder.setSystem(client.get().getName().get());
            }

            AuditLogEntry logEntry = entryBuilder.build();
            log.debug("built logEntry");

            ModuleFramework.RequestContext.Builder ctxBuilder = ModuleFramework.RequestContext.newBuilder();
            ctxBuilder.setMessageId(logEntry.getMessageId());
            ModuleFramework.RequestContext reqCtx = ctxBuilder.build();
            log.debug("built reqCtx");

            AuditLogEntryId auditLogEntryId = auditLogKafkaClient.createAuditLogEntryId(logEntry);
            sendAuditLog(reqCtx, logEntry, auditLogEntryId);
            return auditLogEntryId;
        }
        return null;
    }

    protected void sendAuditLog(ModuleFramework.RequestContext reqCtx, AuditLogEntry logEntry, AuditLogEntryId auditLogEntryId) {
        log.debug("calling sendAuditLog" + logEntry);
        auditLogKafkaClient.sendAuditLog(reqCtx, logEntry, auditLogEntryId);
        log.debug("sent sendAuditLog");
    }
}
