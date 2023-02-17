package dk.bemyndigelsesregister.dao;


import dk.bemyndigelsesregister.dao.DelegatingSystemDAO;
import dk.bemyndigelsesregister.dao.RoleDAO;
import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.DelegatingSystem;
import dk.bemyndigelsesregister.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class RoleDAOTest {
    @Autowired
    private DelegatingSystemDAO delegatingSystemDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Test
    public void testFindRoleByCode() {
        DelegatingSystem system = delegatingSystemDAO.findByCode(TestData.systemCode);

        Role role = roleDAO.findByCode(system.getId(), TestData.roleCode);
        System.out.println(role);
        assertEquals(TestData.roleCode, role.getCode());
        assertEquals(TestData.roleDescription, role.getDescription());
    }

    @Test
    public void testFindRoleBySystem() {
        DelegatingSystem system = delegatingSystemDAO.findByCode(TestData.systemCode);

        List<Role> roles = roleDAO.findBySystem(system.getId());
        System.out.println(roles);
        assertFalse(roles.isEmpty());
    }
}
