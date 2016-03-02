package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by obj on 05-02-2016.
 */
public class RoleDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindRoleByCode() throws Exception {
        DelegatingSystem system = delegatingSystemDao.findByCode(TestData.systemCode);

        Role role = roleDao.findByCode(system.getId(), TestData.roleCode);
        System.out.println(role);
        assertEquals("findByCode should return an object with correct code", TestData.roleCode, role.getCode());
        assertEquals("findByCode should return an object with correct description", TestData.roleDescription, role.getDescription());
    }

    @Test
    public void testFindRoleBySystem() throws Exception {
        DelegatingSystem system = delegatingSystemDao.findByCode(TestData.systemCode);

        List<Role> roles = roleDao.findBySystem(system.getId());
        System.out.println(roles);
        assertFalse("findByCode should return a non-empty list of objects", roles.isEmpty());
    }
}
