package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.domain.Metadata;

import java.time.Instant;

public interface MetadataCache {
    void clear();

    void clear(String domainCode, String systemCode);

    Instant getOldestContent();

    void put(Metadata metadata);

    Metadata get(String domainCode, String systemCode);
}
