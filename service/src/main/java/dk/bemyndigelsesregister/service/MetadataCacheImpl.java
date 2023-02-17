package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.domain.Metadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetadataCacheImpl implements MetadataCache {
    private static final Logger logger = LogManager.getLogger(MetadataCacheImpl.class);

    private Map<String, Metadata> metadataCache = new HashMap<>();

    private Instant oldestContent;

    @Override
    public void clear() {
        metadataCache.clear();
        oldestContent = null;
        logger.info("Metadata cache cleared");
    }

    @Override
    public void clear(String domainCode, String systemCode) {
        if (metadataCache.remove(getKey(domainCode, systemCode)) != null) {
            logger.info("Cleared cached metadata, system=[" + systemCode + "], domain=[" + domainCode + "]");
        }
    }

    public Instant getOldestContent() {
        return oldestContent;
    }

    @Override
    public void put(Metadata metadata) {
        String domainCode = metadata.getDomain().getCode();
        String systemCode = metadata.getSystem().getCode();

        metadataCache.put(getKey(domainCode, systemCode), metadata);

        if (oldestContent == null) {
            oldestContent = Instant.now();
        }

        logger.info("Cached metadata, system=[" + systemCode + "], domain=[" + domainCode + "]");
    }

    @Override
    public Metadata get(String domainCode, String systemCode) {
        return metadataCache.get(getKey(domainCode, systemCode));
    }

    private String getKey(String domainCode, String systemCode) {
        return domainCode + "_" + systemCode;
    }
}
