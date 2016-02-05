package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;

public interface DomainDao {
    Domain get(long id);

    void save(Domain domain);

    Domain findById(String domainId);
}
