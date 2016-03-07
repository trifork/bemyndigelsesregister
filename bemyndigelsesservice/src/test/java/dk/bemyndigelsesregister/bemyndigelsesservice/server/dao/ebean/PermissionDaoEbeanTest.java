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

        assertEquals(3, permissions.size());

        assertEquals(TestData.permissionCode1, permissions.get(0).getCode());
        assertEquals(TestData.permissionDescription1, permissions.get(0).getDescription());

        assertEquals(TestData.permissionCode2, permissions.get(1).getCode());
        assertEquals(TestData.permissionDescription2, permissions.get(1).getDescription());
    }

    @Test
    public void canFindPermissionByCode() throws Exception {
        Permission permission = permissionDao.findByCode(TestData.systemCode, TestData.permissionCode1);

        assertEquals(TestData.permissionCode1, permission.getCode());
        assertEquals(TestData.permissionDescription1, permission.getDescription());
    }
}
