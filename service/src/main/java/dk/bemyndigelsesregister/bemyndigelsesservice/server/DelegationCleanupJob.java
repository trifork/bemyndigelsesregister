package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Repository
public class DelegationCleanupJob {
    private static final Logger logger = Logger.getLogger(DelegationCleanupJob.class);

    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${delegationcleanupjob.enabled:false}")
    private String jobEnabled;

    @Value("${delegationcleanupjob.retentiondays:730}") // 2 years
    private int retentionDays;

    @Value("${delegationcleanupjob.maxrecords:100}")
    private int maxRecords;

    @Inject
    private DelegationDao dao;

    @Scheduled(cron = "${delegationcleanupjob.cron:0 0 1 1/1 * ?}")
    public void start() {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("delegationCleanup job started");
            try {
                cleanup();
            } catch (Exception ex) {
                logger.error("An error occurred during cleanup of old delegation records", ex);
            }
            logger.info("delegationCleanup job ended");
        } else
            logger.info("delegationCleanup job disabled");
    }

    private void cleanup() {
        DateTime beforeDate = DateTime.now().minusDays(retentionDays);
        logger.info("Cleaning delegation records with to-date older than " + retentionDays + " days (dated before " + beforeDate.toString(DATETIME_FORMATTER) + ")");
        int count;
        do {
            count = doCleanup(beforeDate);
            logger.info("Deleted " + count + " old delegation records");
        } while (maxRecords > 0 & count == maxRecords);
    }

    @Transactional
    protected int doCleanup(DateTime beforeDate) {
        return dao.cleanup(beforeDate, maxRecords);
    }
}
