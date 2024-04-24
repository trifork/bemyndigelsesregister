package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.service.DelegationManager;
import dk.bemyndigelsesregister.util.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class DelegationCleanupJob extends AbstractJob {
    private static final Logger logger = LogManager.getLogger(DelegationCleanupJob.class);

    @Value("${delegationcleanupjob.enabled}")
    private String jobEnabled;

    @Value("${delegationcleanupjob.retentiondays}")
    private int retentionDays;

    @Value("${delegationcleanupjob.maxrecords}")
    private int maxRecords;

    @Autowired
    private DelegationManager delegationManager;

    public DelegationCleanupJob() {
        super(logger, "DelegationCleanup");
    }

    @Scheduled(cron = "${delegationcleanupjob.cron}")
    public void start() {
        try {
            initJob();

            if (Boolean.parseBoolean(jobEnabled)) {
                startJob();
                cleanup();
                endJob();
            } else {
                jobDisabled();
            }
        } catch (Exception ex) {
            logger.error("An error occurred during cleanup of old delegation records", ex);
        } finally {
            cleanupJob();
        }
    }

    private void cleanup() {
        Instant beforeDate = DateUtils.plusDays(Instant.now(), -retentionDays);
        logger.info("Cleaning delegation records with to-date older than " + retentionDays + " days (dated before " + DateUtils.format(beforeDate, "yyyy-MM-dd HH:mm:ss") + ")");
        int count;
        do {
            count = doCleanup(beforeDate);
            logger.info("Deleted " + count + " old delegation records");
        } while (maxRecords > 0 & count == maxRecords);
    }

    @Transactional
    protected int doCleanup(Instant beforeDate) {
        return delegationManager.cleanup(beforeDate, maxRecords);
    }
}
