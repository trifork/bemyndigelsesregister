package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import generated.BemyndigelserType;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
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

    @Inject
    Marshaller marshaller;

    @Inject
    NspManager nspManager;

    @Value("${nsp.schema.version}")
    String nspSchemaVersion;


    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() throws IOException {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        doExport(startTime, bemyndigelseDao.findBySidstModificeretGreaterThan(lastRun.getDateTimeValue()));

        updateLastRun(lastRun, startTime);
    }

    public void completeExport() throws IOException {
        SystemVariable lastRun = systemVariableDao.getByName("lastRun");
        final DateTime startTime = systemService.getDateTime();
        doExport(startTime, bemyndigelseDao.list());

        updateLastRun(lastRun, startTime);
    }

    public void doExport(DateTime startTime, List<Bemyndigelse> bemyndigelser) throws IOException {
        logger.info("Starting bemyndigelse sync job");

        if (bemyndigelser == null || bemyndigelser.size() == 0) {
            logger.info("Nothing to export. Stopping export job.");
            return;
        }

        BemyndigelserType bemyndigelserType = new BemyndigelserType();
        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            bemyndigelserType.getBemyndigelse().add(bemyndigelse.toBemyndigelseType());
        }
        bemyndigelserType.setAntalPost(BigInteger.valueOf(bemyndigelser.size()));
        bemyndigelserType.setDato(startTime.toString("yyyyMMdd"));
        bemyndigelserType.setTimeStamp(startTime.toString("HHmmssSSS"));
        bemyndigelserType.setVersion(nspSchemaVersion);

        nspManager.send(bemyndigelserType, startTime);
    }

    private void updateLastRun(SystemVariable lastRun, DateTime startTime) {
        if (!lastRun.getName().equals("lastRun")) {
            throw new IllegalArgumentException("System variable name is NOT \"lastRun\", but " + lastRun.getName());
        }
        lastRun.setDateTimeValue(startTime);
        systemVariableDao.save(lastRun);
    }
}
