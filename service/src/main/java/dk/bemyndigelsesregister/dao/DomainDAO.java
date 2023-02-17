package dk.bemyndigelsesregister.dao;


import dk.bemyndigelsesregister.domain.Domain;

public interface DomainDAO {
    Domain get(long id);

    void save(Domain domain);

    Domain findByCode(String code);
}
