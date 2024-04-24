package dk.bemyndigelsesregister.batch;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public abstract class AbstractJob {
    private final Logger logger;
    private final String jobName;

    private static int jobNo = 1;

    public AbstractJob(Logger logger, String jobName) {
        this.logger = logger;
        this.jobName = jobName;
    }

    protected void initJob() {
        ThreadContext.put("uuid", jobName + "-" + jobNo++); // unique id for logs from this job execution
    }

    protected void startJob() {
        logger.info(jobName + " job started");
    }

    protected void endJob() {
        logger.info(jobName + " job ended");
    }

    protected void jobDisabled() {
        logger.info(jobName + " job disabled");
    }

    protected void cleanupJob() {
        ThreadContext.clearAll();
    }
}
