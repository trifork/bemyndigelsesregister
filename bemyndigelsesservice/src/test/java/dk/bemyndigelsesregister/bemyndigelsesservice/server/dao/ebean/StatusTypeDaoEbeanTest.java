package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.StatusType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatusTypeDaoEbeanTest extends DaoUnitTestSupport {

    @Inject
    StatusTypeDao dao;

    @Test
    public void springIsWorking() throws Exception {
        assertNotNull(dao);
    }

    @Test
    public void canGetById() throws Exception {
        StatusType statusType = dao.get(1l);
        assertNotNull(statusType);
        assertEquals("OK", statusType.getStatus());
    }
}
