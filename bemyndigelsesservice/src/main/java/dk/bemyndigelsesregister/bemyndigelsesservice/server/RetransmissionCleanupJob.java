package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.MessageRetransmissionDao;
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
public class RetransmissionCleanupJob {
    private static final Logger logger = Logger.getLogger(RetransmissionCleanupJob.class);

    private final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${retransmissioncleanupjob.enabled:false}")
    private String jobEnabled;

    @Value("${retransmissioncleanupjob.retentiondays:14}")
    private int retentionDays;

    @Value("${retransmissioncleanupjob.maxrecords:1000}")
    private int maxRecords;

    @Inject
    private MessageRetransmissionDao dao;

    @Scheduled(cron = "${retransmissioncleanupjob.cron:0 30 0 1/1 * ?}")
    public void start() {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("RetransmissionCleanup job started");
            try {
                cleanup();
            } catch (Exception ex) {
                logger.error("An error occurred during cleanup of message_retransmission records", ex);
            }
            logger.info("RetransmissionCleanup job ended");
        } else
            logger.info("RetransmissionCleanup job disabled");
    }

    private void cleanup() {
        DateTime beforeDate = DateTime.now().minusDays(retentionDays);
        logger.info("Cleaning message_retransmission records older than " + retentionDays + " days (dated before " + beforeDate.toString(DATETIME_FORMATTER) + ")");
        int count;
        do {
            count = dao.cleanup(beforeDate, maxRecords);
            logger.info("Deleted " + count + " message_retransmission records");
        } while (maxRecords > 0 & count == maxRecords);
    }
}
