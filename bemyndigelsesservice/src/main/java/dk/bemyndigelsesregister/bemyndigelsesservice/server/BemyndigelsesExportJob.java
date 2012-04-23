package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import generated.BemyndigelserType;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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

    private static DateTime lastRun = new DateTime(0l); //TODO: persist?

    @Inject
    BemyndigelseDao bemyndigelseDao;

    @Inject
    SystemService systemService;

    @Inject
    Marshaller marshaller;

    @Inject
    NspManager nspManager;


    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() throws IOException {
        final DateTime startTime = systemService.getDateTime();
        doExport(lastRun, startTime);

        updateLastRun(startTime);
    }

    public void doExport(DateTime modifiedSince, DateTime startTime) throws IOException {
        logger.debug("Starting bemyndigelse sync job");
        List<Bemyndigelse> bemyndigelser = bemyndigelseDao.findBySidstModificeretGreaterThan(modifiedSince);

        BemyndigelserType bemyndigelserType = new BemyndigelserType();
        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            bemyndigelserType.getBemyndigelse().add(bemyndigelse.toBemyndigelseType());
        }
        bemyndigelserType.setAntalPost(BigInteger.valueOf(bemyndigelser.size()));
        bemyndigelserType.setDato(startTime.toString("yyyyMMdd"));
        bemyndigelserType.setTimeStamp(startTime.toString("HHmmssSSS"));
        bemyndigelserType.setVersion("v00001");

        nspManager.send(bemyndigelserType, startTime);
    }

    private void updateLastRun(DateTime startTime) {
        lastRun = startTime;
    }
}
