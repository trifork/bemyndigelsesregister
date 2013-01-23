package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelser._2012._04.Bemyndigelser;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Repository
public class BemyndigelsesExportJob {
    private static Logger logger = Logger.getLogger(BemyndigelsesExportJob.class);

    @Inject
    BemyndigelseDao bemyndigelseDao;

    @Inject
    SystemVariableDao systemVariableDao;

    @Inject
    SystemService systemService;

    @Inject @Named("nspManagerSftp")
    NspManager nspManager;

    @Value("${nsp.schema.version}")
    String nspSchemaVersion;


    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    @Transactional
    public void startExport() throws IOException {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        doExport(startTime, bemyndigelseDao.findBySidstModificeretGreaterThan(lastRun.getDateTimeValue()));

        updateLastRun(lastRun, startTime);
    }

    @Transactional
    public void completeExport() {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        try {
            doExport(startTime, bemyndigelseDao.list());
            updateLastRun(lastRun, startTime);
        } catch (IOException e) {
            logger.error("Export failed", e);
        }
    }

    public void doExport(DateTime startTime, List<Bemyndigelse> bemyndigelser) throws IOException {
        logger.info("Starting bemyndigelse sync job");

        if (bemyndigelser == null || bemyndigelser.size() == 0) {
            logger.info("Nothing to export. Stopping export job.");
            return;
        }
        else {
            logger.info("Exporting " + bemyndigelser.size() + " entries");
        }

        Bemyndigelser bemyndigelserType = new Bemyndigelser();
        for (Bemyndigelse bemyndigelse : bemyndigelser) {
        	if(bemyndigelse.getStatus() == Status.GODKENDT) {
                bemyndigelserType.getBemyndigelse().add(bemyndigelse.toBemyndigelseType());
            }
        }
        bemyndigelserType.setAntalPost(BigInteger.valueOf(bemyndigelserType.getBemyndigelse().size()));
        bemyndigelserType.setDato(startTime.toString("yyyyMMdd"));
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
