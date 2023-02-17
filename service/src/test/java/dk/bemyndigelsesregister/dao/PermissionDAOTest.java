package dk.bemyndigelsesregister.dao;


import dk.bemyndigelsesregister.dao.DelegatingSystemDAO;
import dk.bemyndigelsesregister.dao.PermissionDAO;
import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PermissionDAOTest {
    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private DelegatingSystemDAO delegatingSystemDAO;

    @Test
    public void canFindPermissionBySystem() {
        List<Permission> permissions = permissionDAO.findBySystem(delegatingSystemDAO.get(1).getId());

        assertEquals(3, permissions.size());

        assertEquals(TestData.permissionCode1, permissions.get(0).getCode());
        assertEquals(TestData.permissionDescription1, permissions.get(0).getDescription());

        assertEquals(TestData.permissionCode2, permissions.get(1).getCode());
        assertEquals(TestData.permissionDescription2, permissions.get(1).getDescription());
    }

    @Test
    public void canFindPermissionByCode() {
        Permission permission = permissionDAO.findByCode(delegatingSystemDAO.findByCode(TestData.systemCode).getId(), TestData.permissionCode1);

        assertEquals(TestData.permissionCode1, permission.getCode());
        assertEquals(TestData.permissionDescription1, permission.getDescription());
    }
}
