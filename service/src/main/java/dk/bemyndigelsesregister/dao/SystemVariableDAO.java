package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.SystemVariable;

public interface SystemVariableDAO {
    SystemVariable get(long id);

    SystemVariable getByName(String name);

    void save(SystemVariable systemVariable);
}
