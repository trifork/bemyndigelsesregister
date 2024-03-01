package dk.bemyndigelsesregister.dao;


import dk.bemyndigelsesregister.domain.DelegatingSystem;
import dk.bemyndigelsesregister.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testSaveEducationCodes() {
        DelegatingSystem system = delegatingSystemDAO.findByCode(TestData.systemCode);

        // Test with no education codes
        Role role = new Role();
        role.setSystem(system);
        role.setCode("some code");
        role.setDescription("some description");
        role.setEducationCodes(null);
        roleDAO.save(role);

        Role resultingRole = roleDAO.get(role.getId());
        assertNull(resultingRole.getEducationCodes());

        // Test with one education code
        role = new Role();
        role.setSystem(system);
        role.setCode("some code");
        role.setDescription("some description");
        role.setEducationCodes(List.of("7170"));
        roleDAO.save(role);

        resultingRole = roleDAO.get(role.getId());
        assertEquals(1, resultingRole.getEducationCodes().size());
        assertEquals("7170", resultingRole.getEducationCodes().get(0));

        // Test with several education codes
        role = new Role();
        role.setSystem(system);
        role.setCode("some code");
        role.setDescription("some description");
        role.setEducationCodes(List.of("0001,0002,0003"));
        roleDAO.save(role);

        resultingRole = roleDAO.get(role.getId());
        assertEquals(3, resultingRole.getEducationCodes().size());
        assertEquals("0001", resultingRole.getEducationCodes().get(0));
        assertEquals("0002", resultingRole.getEducationCodes().get(1));
        assertEquals("0003", resultingRole.getEducationCodes().get(2));
    }

}
