package dk.bemyndigelsesregister.bemyndigelsesservice.server;


import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;

import java.util.List;

/**
 * Created by obj on 12-02-2016.
 */
public interface MetadataManager {

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


    void clearCache();
}
