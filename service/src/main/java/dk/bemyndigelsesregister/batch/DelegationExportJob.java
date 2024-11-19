package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.batch.exportmodel.Delegations;
import dk.bemyndigelsesregister.dao.DelegationDAO;
import dk.bemyndigelsesregister.dao.SystemVariableDAO;
import dk.bemyndigelsesregister.domain.*;
import dk.bemyndigelsesregister.service.DelegationManager;
import dk.bemyndigelsesregister.service.SystemService;
import dk.bemyndigelsesregister.util.DateUtils;
import dk.bemyndigelsesregister.ws.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

@Component
public class DelegationExportJob extends AbstractJob {
    private static final Logger logger = LogManager.getLogger(DelegationExportJob.class);
    private static final String LAST_RUN_SYSTEM_VARIABLE = "lastRun";

    @Autowired
    private DelegationDAO delegationDAO;

    @Autowired
    private DelegationManager delegationManager;

    @Autowired
    private SystemVariableDAO systemVariableDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UploadManager uploadManager;

    @Value("${nsp.schema.version}")
    private String nspSchemaVersion;

    @Value("${bemyndigelsesexportjob.enabled}")
    String jobEnabled;

    @Value("${bemyndigelsesexportjob.batchsize}")
    Integer batchSize;

    @Value("${bemyndigelsesexportjob.retentiondays}")
    Integer retentionDays;

    @Value("${bemyndigelsesexportjob.skiplist:}")
    private List<String> skipList;

    private int batchNo;

    public DelegationExportJob() {
        super(logger, "DelegationExport");
    }

    @PostConstruct
    public void init() {
        if (Boolean.parseBoolean(jobEnabled)) {
            logger.info("DelegationExportJob initializing, batchSize=" + batchSize + ", retentionDays=" + retentionDays + ", skipList=" + skipList);
        }
    }

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() {
        try {
            initJob();

            if (Boolean.parseBoolean(jobEnabled)) {
                startJob();
                RequestContext.get().setActingUser("DelegationExportJob");

                batchNo = 1;
                Instant startTime = Instant.now();
                SystemVariable lastRun = systemVariableDAO.getByName(LAST_RUN_SYSTEM_VARIABLE);
                if (lastRun == null) {
                    lastRun = new SystemVariable();
                    lastRun.setName(LAST_RUN_SYSTEM_VARIABLE);
                    lastRun.setInstantValue(startTime);
                }

                Instant fromIncluding = DateUtils.plusMinutes(lastRun.getInstantValue(), -1);
                Instant toExcluding = DateUtils.plusMinutes(startTime, -1);

                // export changed delegations
                exportChangedDelegations(startTime, delegationDAO.findByModifiedInPeriod(fromIncluding, toExcluding, skipList));

                updateLastRun(lastRun, startTime);

                systemService.cleanupTempDir(retentionDays);

                endJob();
            } else {
                jobDisabled();
            }
        } catch (Exception ex) {
            logger.error("An error occurred during export of changed delegations", ex);
        } finally {
            RequestContext.clear();
            cleanupJob();
        }
    }

    public int exportChangedDelegations(Instant startTime, List<Long> delegationIds) {
        int exportCount = 0;

        if (delegationIds == null || delegationIds.size() == 0) {
            logger.info("No changed delegations to export");
        } else {
            logger.info("Exporting " + delegationIds.size() + " changed delegations");

            // export in batches
            int startIndex = 0;
            while (startIndex < delegationIds.size()) {
                int endIndex = startIndex + batchSize;
                if (endIndex > delegationIds.size())
                    endIndex = delegationIds.size();

                logger.info("  Exporting batch " + batchNo + " (index " + startIndex + " - " + endIndex + ")");
                exportCount += exportBatch(startTime, delegationIds.subList(startIndex, endIndex));

                startIndex = endIndex;
            }
        }

        return exportCount;
    }

    public int exportBatch(Instant startTime, List<Long> delegationIds) {
        int exportCount = 0;

        Delegations exportData = new Delegations();
        for (Long delegationId : delegationIds) {
            Delegation delegation = delegationManager.getDelegation(delegationId);
            if (delegation.getState() == Status.GODKENDT) {
                for (DelegationPermission delegationPermission : delegation.getDelegationPermissions()) {
                    if (!Metadata.ASTERISK_PERMISSION_CODE.equals(delegationPermission.getPermissionCode())) {
                        exportData.addDelegation(delegationPermission.getCode(), delegation.getDelegatorCpr(), delegation.getDelegateeCpr(), delegation.getDelegateeCvr(), delegation.getSystemCode(), delegation.getState().value(), delegation.getRoleCode(), delegationPermission.getPermissionCode(), delegation.getCreated(), delegation.getLastModified(), delegation.getEffectiveFrom(), delegation.getEffectiveTo());
                        exportCount++;
                    }
                }
            }
        }

        logger.info("    " + exportCount + " records exported");

        if (exportCount > 0) {
            exportData.setDate(DateUtils.format(startTime, "yyyyMMdd"));
            exportData.setTimeStamp(DateUtils.format(startTime, "HHmmssSSS"));
            exportData.setVersion(nspSchemaVersion);

            uploadManager.upload(exportData, startTime, batchNo);

            batchNo++; // Note: batchNo will only increase when delegations were actually exported
        }

        return exportCount;
    }

    private void updateLastRun(SystemVariable lastRun, Instant startTime) {
        if (!lastRun.getName().equals(LAST_RUN_SYSTEM_VARIABLE)) {
            throw new IllegalArgumentException("System variable name is NOT \"lastRun\", but " + lastRun.getName());
        }
        lastRun.setInstantValue(startTime);
        systemVariableDAO.save(lastRun);
    }
}
