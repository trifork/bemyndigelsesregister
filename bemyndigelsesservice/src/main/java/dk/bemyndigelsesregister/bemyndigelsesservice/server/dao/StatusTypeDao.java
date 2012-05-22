package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.StatusType;

public interface StatusTypeDao {
    StatusType get(long id);

    StatusType findByKode(String kode);
}
