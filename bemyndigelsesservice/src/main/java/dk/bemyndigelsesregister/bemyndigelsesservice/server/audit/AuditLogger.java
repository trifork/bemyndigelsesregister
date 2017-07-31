package dk.bemyndigelsesregister.bemyndigelsesservice.server.audit;

import com.trifork.dgws.DgwsRequestContext;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.fmk.auditlog.client.AuditLogKafkaClient;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntry;
import dk.nsi.fmk.auditlog.data.proto.AuditLog.AuditLogEntryId;
import dk.nsi.fmk.moduleframework.data.ModuleFramework;
import org.apache.log4j.Logger;

import javax.inject.Inject;
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
                InputStream is = AuditLogger.class.getResourceAsStream("/kafka-producer.properties");
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);

                    auditLogKafkaClient = new AuditLogKafkaClient(props, useMock);
                    configured = true;
                    log.info("AuditLogKafkaClient configured, useMock=" + useMock);
                } else {
                    log.warn("Could not configure AuditLogKafkaClient - kafka-producer.properties not found!");
                }
            } catch (Exception e) {
                log.error("Could not configure AuditLogKafkaClient - got exception", e);
            }
        } else {
            log.info("AuditLogging disabled.");
        }
    }

    public AuditLogEntryId log(String method) {
        if (configured) {
            AuditLogEntry.Builder entryBuilder = AuditLogEntry.newBuilder();

            String messageId = RequestContext.get().getMessageId();
            entryBuilder.setRequestId(messageId);
            entryBuilder.setMessageId(messageId);
            entryBuilder.setMethod(method);
            entryBuilder.setTimestamp(System.currentTimeMillis());
            int authLevel = dgwsRequestContext.getIdCardData().getAuthenticationLevel();
            entryBuilder.setAuthLevel(authLevel);

            if (authLevel > 3) {
                entryBuilder.setCpr(dgwsRequestContext.getIdCardUserLog().cpr);
                entryBuilder.setRole(dgwsRequestContext.getIdCardUserLog().role);
                entryBuilder.setAdditionalUserInfo(dgwsRequestContext.getIdCardUserLog().emailAddress);
                entryBuilder.setAuthorizationNumber(dgwsRequestContext.getIdCardUserLog().authorisationCode);
            }

            entryBuilder.setCvr(dgwsRequestContext.getIdCardSystemLog().getCareProviderId());
            entryBuilder.setOrganisationName(dgwsRequestContext.getIdCardSystemLog().getCareProviderName());
            entryBuilder.setSystem(dgwsRequestContext.getIdCardSystemLog().getItSystemName());

            AuditLogEntry logEntry = entryBuilder.build();

            ModuleFramework.RequestContext.Builder ctxBuilder = ModuleFramework.RequestContext.newBuilder();
            ctxBuilder.setClientSystem("BEM");
            ctxBuilder.setClientVersionOfService(systemService.getImplementationBuild());
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
