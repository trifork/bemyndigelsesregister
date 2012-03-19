package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Authorization;

public interface AuthorizationDao {
    Authorization get(long id);
}
