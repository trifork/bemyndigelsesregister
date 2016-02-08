package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by obj on 08-02-2016.
 */
public class PermissionDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindPermissionBySystem() throws Exception {
        List<Permission> permissions = permissionDao.findBySystem(delegatingSystemDao.get(1).getDomainId());

        assertEquals(2, permissions.size());
        assertEquals("R01", permissions.get(0).getDomainId());
        assertEquals("R02", permissions.get(1).getDomainId());
    }
}
