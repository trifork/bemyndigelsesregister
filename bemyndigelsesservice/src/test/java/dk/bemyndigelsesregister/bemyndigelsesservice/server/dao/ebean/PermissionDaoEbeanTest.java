package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by obj on 08-02-2016.
 */
public class PermissionDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindPermissionBySystem() throws Exception {
        List<Permission> permissions = permissionDao.findBySystem(delegatingSystemDao.get(1).getId());

        assertEquals(2, permissions.size());

        assertEquals(TestData.permissionCode1, permissions.get(0).getDomainId());
        assertEquals(TestData.permissionDescription1, permissions.get(0).getDescription());

        assertEquals(TestData.permissionCode2, permissions.get(1).getDomainId());
        assertEquals(TestData.permissionDescription2, permissions.get(1).getDescription());
    }
}
