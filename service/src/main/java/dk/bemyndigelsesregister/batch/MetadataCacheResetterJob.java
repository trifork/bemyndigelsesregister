package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.dao.SystemVariableDAO;
import dk.bemyndigelsesregister.domain.SystemVariable;
import dk.bemyndigelsesregister.service.MetadataCache;
import dk.bemyndigelsesregister.service.MetadataManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Job to clear metadata cache after metadata updates - ensures clear when another server updates
 */
@Component
public class MetadataCacheResetterJob {
    private static final Logger logger = LogManager.getLogger(MetadataCacheResetterJob.class);

    @Autowired
    private SystemVariableDAO systemVariableDAO;

    @Autowired
    private MetadataCache metadataCache;

    @Value("${metadatacacheresetterjob.enabled}")
    private String jobEnabled;

    @Scheduled(cron = "${metadatacacheresetterjob.cron}")
    public void start() {
        if (Boolean.valueOf(jobEnabled)) {
            logger.info("MetadataCacheResetter job started");

            SystemVariable lastUpdate = systemVariableDAO.getByName(MetadataManager.LAST_METADATA_UPDATE_SYSTEM_VARIABLE);
            if (lastUpdate != null) {
                Instant oldestContent = metadataCache.getOldestContent();
                Instant updateTime = lastUpdate.getInstantValue();
                if (oldestContent != null && oldestContent.isBefore(updateTime)) {
                    logger.info("Oldest metadata cache content " + oldestContent + " before update time " + updateTime + ", clear cache");
                    metadataCache.clear();
                }
            }

            logger.info("MetadataCacheResetter job ended");
        } else
            logger.info("MetadataCacheResetter job disabled");
    }
}
