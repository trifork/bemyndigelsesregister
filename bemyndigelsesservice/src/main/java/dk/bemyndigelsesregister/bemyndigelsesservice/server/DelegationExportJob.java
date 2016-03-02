package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
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
    private static Logger logger = Logger.getLogger(DelegationExportJob.class);

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

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    @Transactional
    public void startExport() throws IOException {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        doExport(startTime, delegationDao.findByLastModifiedGreaterThanOrEquals(lastRun.getDateTimeValue()));

        updateLastRun(lastRun, startTime);
    }

    @Transactional
    public void completeExport() {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        try {
            doExport(startTime, delegationDao.list());
            updateLastRun(lastRun, startTime);
        } catch (IOException e) {
            logger.error("Export failed", e);
        }
    }

    public void doExport(DateTime startTime, List<Delegation> delegations) throws IOException {
        logger.info("Starting bemyndigelse sync job");

        if (delegations == null || delegations.size() == 0) {
            logger.info("Nothing to export. Stopping export job.");
            return;
        } else {
            logger.info("Exporting " + delegations.size() + " entries");
        }

        Delegations bemyndigelserType = new Delegations();
        for (Delegation bemyndigelse : delegations) {
            if (bemyndigelse.getState() == State.GODKENDT) {
                bemyndigelserType.addDelegation(bemyndigelse);
            }
        }
        bemyndigelserType.setDate(startTime.toString("yyyyMMdd"));
        bemyndigelserType.setTimeStamp(startTime.toString("HHmmssSSS"));
        bemyndigelserType.setVersion(nspSchemaVersion);

        nspManager.send(bemyndigelserType, startTime);

        logger.info("Completed export");
    }

    private void updateLastRun(SystemVariable lastRun, DateTime startTime) {
        if (!lastRun.getName().equals("lastRun")) {
            throw new IllegalArgumentException("System variable name is NOT \"lastRun\", but " + lastRun.getName());
        }
        lastRun.setDateTimeValue(startTime);
        systemVariableDao.save(lastRun);
    }
}
