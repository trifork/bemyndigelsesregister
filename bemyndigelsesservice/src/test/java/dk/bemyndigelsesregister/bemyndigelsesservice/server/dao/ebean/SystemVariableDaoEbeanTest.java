package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.SystemVariable;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

public class SystemVariableDaoEbeanTest extends DaoUnitTestSupport {

    @Inject
    SystemVariableDao systemVariableDao;

    @Test
    public void canReadGetVariableByName() throws Exception {
        assertEquals("den gode test", systemVariableDao.getByName("testVariable").getValue());
    }

    @Test
    public void canUpdateVariable() throws Exception {
        SystemVariable sv = systemVariableDao.getByName("testVariable");
        sv.setValue("Den Rigtigt Gode Test");
        systemVariableDao.save(sv);

        assertEquals("Den Rigtigt Gode Test", systemVariableDao.getByName("testVariable").getValue());
    }
}
