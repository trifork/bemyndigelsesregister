package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

import com.trifork.dgws.DgwsRequestContext;
import com.trifork.dgws.IdCardUserLog;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.fmk.auditlog.client.AuditLogKafkaClient;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntry;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntryId;
import dk.nsi.fmk.moduleframework.data.ModuleFramework;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Kafka auditlogger
 */
public class AuditLogger {
    private static final Logger log = Logger.getLogger(AuditLogger.class);

    @Inject
    SystemService systemService;

    @Inject
    DgwsRequestContext dgwsRequestContext;

    // instance
    private boolean loggingEnabled;
    private boolean useMock;
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

    public AuditLogEntryId log(String method, String delegateeCpr) {
        if (configured) {
            AuditLogEntry.Builder entryBuilder = AuditLogEntry.newBuilder();

            String messageId = RequestContext.get().getMessageId();
            entryBuilder.setAccessType("Soap");
            entryBuilder.setServerSystemName("BEM");
            entryBuilder.setServiceVersion(systemService.getImplementationBuild());
            entryBuilder.setRequestId(messageId);
            entryBuilder.setMessageId(messageId);
            entryBuilder.setMethod(method);
            entryBuilder.setTimestamp(System.currentTimeMillis());
            int authLevel = dgwsRequestContext.getIdCardData().getAuthenticationLevel();
            entryBuilder.setAuthLevel(authLevel);

            if (authLevel > 3) {
                IdCardUserLog userLog = dgwsRequestContext.getIdCardUserLog();
                entryBuilder.setCpr(userLog.cpr);
                if (delegateeCpr != null) {
                    entryBuilder.setPersonCPR(delegateeCpr);
                }
                if (userLog.role != null) {
                    entryBuilder.setRole(userLog.role);
                }
                if (userLog.authorisationCode != null) {
                    entryBuilder.setAuthorizationNumber(userLog.authorisationCode);
                }

                StringBuilder b = new StringBuilder();
                if (userLog.givenName != null) {
                    b.append(userLog.givenName);
                }
                if (userLog.surname != null && !userLog.surname.isEmpty()) {
                    if (b.length() > 0) {
                        b.append(" ");
                    }
                    b.append(userLog.surname);
                }
                if (b.length() > 0) {
                    entryBuilder.setUserName(b.toString());
                }
            }

            entryBuilder.setCvr(dgwsRequestContext.getIdCardSystemLog().getCareProviderId());
            entryBuilder.setOrganisationName(dgwsRequestContext.getIdCardSystemLog().getCareProviderName());
            entryBuilder.setSystem(dgwsRequestContext.getIdCardSystemLog().getItSystemName());

            AuditLogEntry logEntry = entryBuilder.build();

            ModuleFramework.RequestContext.Builder ctxBuilder = ModuleFramework.RequestContext.newBuilder();
            ctxBuilder.setMessageId(logEntry.getMessageId());
            ModuleFramework.RequestContext reqCtx = ctxBuilder.build();

            AuditLogEntryId auditLogEntryId = auditLogKafkaClient.createAuditLogEntryId(logEntry);
            sendAuditLog(reqCtx, logEntry, auditLogEntryId);
            return auditLogEntryId;
        }
        return null;
    }

    protected void sendAuditLog(ModuleFramework.RequestContext reqCtx, AuditLogEntry logEntry, AuditLogEntryId auditLogEntryId) {
        auditLogKafkaClient.sendAuditLog(reqCtx, logEntry, auditLogEntryId);
    }
}
