package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import dk.bemyndigelsesregister.shared.service.SystemService;
import generated.BemyndigelserType;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
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

    @Scheduled(cron = "${bemyndigelsesexportjob.cron}")
    public void startExport() {
        final DateTime startTime = systemService.getDateTime();
        startExport(lastRun, startTime);

        updateLastRun(startTime);
    }

    public void startExport(DateTime modifiedSince, DateTime startTime) {
        logger.debug("Starting bemyndigelse sync job");
        List<Bemyndigelse> bemyndigelser = bemyndigelseDao.findBySidstModificeretGreaterThan(modifiedSince);

        BemyndigelserType bemyndigelserType = new BemyndigelserType();
        for (Bemyndigelse bemyndigelse : bemyndigelser) {
            bemyndigelserType.getBemyndigelse().add(bemyndigelse.toBemyndigelseType());
        }
        bemyndigelserType.setAntalPost(BigInteger.valueOf(bemyndigelser.size()));
        bemyndigelserType.setDato(startTime.toString("yyyyMMdd"));
        bemyndigelserType.setTimeStamp(startTime.toString()); //TODO: format
        bemyndigelserType.setVersion("v00001");
    }

    private void updateLastRun(DateTime startTime) {
        lastRun = startTime;
    }
}
