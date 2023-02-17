package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.domain.Metadata;

import java.util.List;

public interface MetadataManager {
    String LAST_METADATA_UPDATE_SYSTEM_VARIABLE = "lastMetadataUpdate";

    /**
     * Updates metadata
     */
    void putMetadata(Metadata metadata);

    /**
     * returns metadata
     * @return
     */
    Metadata getMetadata(String domainCode, String systemCode);

    /**
     * returns metadata
     * @return
     */
    List<Metadata> getAllMetadata(String domainCode);
}
