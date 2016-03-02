package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;

public interface DomainDao {
    Domain get(long id);

    void save(Domain domain);

    Domain findByCode(String code);
}
