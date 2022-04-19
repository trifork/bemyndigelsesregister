package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import org.springframework.stereotype.Repository;

@Repository
public class SystemVariableDaoEbean extends SupportDao<SystemVariable> implements SystemVariableDao {

    protected SystemVariableDaoEbean() {
        super(SystemVariable.class);
    }

    @Override
    public SystemVariable getByName(String name) {
        return query().where().eq("name", name).findUnique();
    }
}
