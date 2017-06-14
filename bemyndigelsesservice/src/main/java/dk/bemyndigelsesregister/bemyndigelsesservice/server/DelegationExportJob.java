package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;

@Repository
public class DelegationExportJob {
    private static final Logger logger = Logger.getLogger(DelegationExportJob.class);
    private static final String LAST_RUN_SYSTEM_VARIABLE = "lastRun";

    @Inject
    DelegationDao delegationDao;

    @Inject
    SystemVariableDao systemVariableDao;

    @Inject
    SystemService systemService;

    @Inject
    @Named("nspManagerSftp")
    NspManager nspManager;

    @Value("${nsp.schema.version}")
    String nspSchemaVersion;

    @Value("${bemyndigelsesexportjob.enabled:true}")
    String jobEnabled;

    @Value("${bemyndigelsesexportjob.batchsize:5000}")
    Integer batchSize;

    private int batchNo;

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() throws IOException {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("DelegationExport job started");

            batchNo = 1;
            SystemVariable lastRun = systemVariableDao.getByName(LAST_RUN_SYSTEM_VARIABLE);
            DateTime startTime = systemService.getDateTime();

            DateTime fromIncluding = lastRun.getDateTimeValue();
            fromIncluding = fromIncluding.minusMinutes(1);

            DateTime toExcluding = startTime;
            toExcluding = toExcluding.minusMinutes(1);

            // export changed delegations
            exportChangedDelegations(startTime, delegationDao.findByModifiedInPeriod(fromIncluding, toExcluding));

            updateLastRun(lastRun, startTime);
            logger.info("DelegationExport job ended");
        } else
            logger.info("DelegationExport job disabled");
    }

    @Transactional
    public void completeExport() {
        logger.info("Complete DelegationExport started");

        batchNo = 1;
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        DateTime startTime = systemService.getDateTime();

        exportChangedDelegations(startTime, delegationDao.findByModifiedInPeriod(null, null));
        updateLastRun(lastRun, startTime);

        logger.info("Complete DelegationExport ended");
    }

    public void exportChangedDelegations(DateTime startTime, List<Long> delegationIds) {
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
                exportBatch(startTime, delegationIds.subList(startIndex, endIndex));

                startIndex = endIndex;
            }
        }
    }

    public void exportBatch(DateTime startTime, List<Long> delegationIds) {
        int exportCount = 0;

        Delegations exportData = new Delegations();
        for (Long delegationId : delegationIds) {
            Delegation delegation = delegationDao.get(delegationId);
            if (delegation.getState() == State.GODKENDT) {
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
            exportData.setDate(startTime.toString("yyyyMMdd"));
            exportData.setTimeStamp(startTime.toString("HHmmssSSS"));
            exportData.setVersion(nspSchemaVersion);

            nspManager.send(exportData, startTime, batchNo);

            batchNo++; // Note: batchNo will only increase when delegations were actually exported
        }
    }

    private void updateLastRun(SystemVariable lastRun, DateTime startTime) {
        if (!lastRun.getName().equals(LAST_RUN_SYSTEM_VARIABLE)) {
            throw new IllegalArgumentException("System variable name is NOT \"lastRun\", but " + lastRun.getName());
        }
        lastRun.setDateTimeValue(startTime);
        systemVariableDao.save(lastRun);
    }
}
