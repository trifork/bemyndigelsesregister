package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;

public interface SystemVariableDao {
    SystemVariable get(long id);

    SystemVariable getByName(String name);

    void save(SystemVariable systemVariable);
}
