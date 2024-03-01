package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Metadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MetadataManagerImplTest {
    @Autowired
    MetadataManager manager;

    private static final String domain = "MyExampleDomain";
    private static final String system = "MyExampleSystem";

    @Test
    public void canClearExistingMetadata() {
        Metadata p = new Metadata(domain, system, system);

        p.addRole("MyRoleCode", "MyRoleDescription", null);
        p.addPermission("MyPermissionCode", "MyPermissionDescription");
        p.addDelegatablePermission("MyRoleCode", "MyPermissionCode", "MyPermissionDescription", true);

        manager.putMetadata(p);

        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(domain, g.getDomain().getCode());
        assertEquals(system, g.getSystem().getCode());
        assertEquals(1, g.getRoles().size());
        assertEquals(1, g.getPermissions().size());
        assertEquals(1, g.getDelegatablePermissions().size());

        p = new Metadata(domain, system, system);
        manager.putMetadata(p);
        g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(domain, g.getDomain().getCode());
        assertEquals(system, g.getSystem().getCode());
        assertTrue(g.getRoles().isEmpty());
        assertTrue(g.getPermissions().isEmpty());
        assertTrue(g.getDelegatablePermissions().isEmpty());
    }

    @Test
    public void canChangeSystemDescription() {
        String newDescription = "Changed system description";

        Metadata p1 = new Metadata(domain, system, system);
        manager.putMetadata(p1);

        Metadata p2 = manager.getMetadata(domain, system);
        p2.getSystem().setDescription(newDescription);
        manager.putMetadata(p2);

        Metadata g = manager.getMetadata(domain, system);

        assertEquals(newDescription, g.getSystem().getDescription());
    }

    @Test
    public void canCreateNewMetadata() {
        Metadata p = new Metadata(domain, system, system);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(p.getDomain().getCode(), g.getDomain().getCode());
        assertEquals(p.getSystem().getCode(), g.getSystem().getCode());
        assertEquals(0, g.getRoles().size());
        assertEquals(0, g.getPermissions().size());
        assertEquals(0, g.getDelegatablePermissions().size());
    }

    @Test
    public void canUpdateRoles() {
        Metadata p = new Metadata(domain, system, system);
        p.addRole("MyRole", "MyRoleDescription", List.of("0001", "0002"));
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(1, g.getRoles().size());
        assertEquals(p.getRoles().get(0).getCode(), g.getRoles().get(0).getCode());
        assertEquals(p.getRoles().get(0).getDescription(), g.getRoles().get(0).getDescription());
        assertEquals(p.getRoles().get(0).getEducationCodes(), g.getRoles().get(0).getEducationCodes());
    }

    @Test
    public void canRemoveRole() {
        Metadata p = new Metadata(domain, system, system);
        p.addRole(TestData.roleCode, TestData.roleDescription, TestData.roleEducationCodes);
        p.addRole("RoleToRemove", "RoleToRemoveDescription", null);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addPermission(TestData.permissionCode2, TestData.permissionDescription2);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode2, TestData.permissionDescription2, true);
        p.addDelegatablePermission("RoleToRemove", TestData.permissionCode1, TestData.permissionDescription1, true);
        p.addDelegatablePermission("RoleToRemove", TestData.permissionCode2, TestData.permissionDescription2, true);
        manager.putMetadata(p);

        p = new Metadata(domain, system, system);
        p.addRole(TestData.roleCode, TestData.roleDescription, TestData.roleEducationCodes);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addPermission(TestData.permissionCode2, TestData.permissionDescription2);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode2, TestData.permissionDescription2, true);
        manager.putMetadata(p);

        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(1, g.getRoles().size());
        assertEquals(TestData.roleCode, g.getRoles().get(0).getCode());
        assertEquals(TestData.roleDescription, g.getRoles().get(0).getDescription());
        assertEquals(TestData.roleEducationCodes, g.getRoles().get(0).getEducationCodes());
    }

    @Test
    public void canUpdatePermissions() {
        Metadata p = new Metadata(domain, system, system);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addPermission(TestData.permissionCode2, TestData.permissionDescription2);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(2, g.getPermissions().size());
        for (int i = 0; i < 2; i++) {
            assertEquals(p.getPermissions().get(i).getCode(), g.getPermissions().get(i).getCode());
            assertEquals(p.getPermissions().get(i).getDescription(), g.getPermissions().get(i).getDescription());
        }
    }

    @Test
    public void canRemovePermission() {
        Metadata p = new Metadata(domain, system, system);
        p.addRole(TestData.roleCode, TestData.roleDescription, TestData.roleEducationCodes);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addPermission(TestData.permissionCode2, TestData.permissionDescription2);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode2, TestData.permissionDescription2, true);
        manager.putMetadata(p);

        p = new Metadata(domain, system, system);
        p.addRole(TestData.roleCode, TestData.roleDescription, TestData.roleEducationCodes);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        manager.putMetadata(p);

        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(1, g.getPermissions().size());
        assertEquals(TestData.permissionCode1, g.getPermissions().get(0).getCode());
        assertEquals(TestData.permissionDescription1, g.getPermissions().get(0).getDescription());
    }

    @Test
    public void canUpdateDelegatablePermission() {
        Metadata p = new Metadata(domain, system, system);
        p.addRole(TestData.roleCode, TestData.roleDescription, TestData.roleEducationCodes);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(domain, system);

        assertNotNull(g);
        assertEquals(1, g.getDelegatablePermissions().size());
        assertEquals(p.getDelegatablePermissions().get(0).getRole().getCode(), g.getDelegatablePermissions().get(0).getRole().getCode());
        assertEquals(p.getDelegatablePermissions().get(0).getPermission().getCode(), g.getDelegatablePermissions().get(0).getPermission().getCode());
    }

    @Test
    public void cannotReferenceUnknownRole() {
        try {
            Metadata p = new Metadata(domain, system, system);
            p.addDelegatablePermission("Unknown", TestData.permissionCode1, TestData.permissionDescription1, true);
            manager.putMetadata(p);

            fail("Saving metadata with delegatable permission having unknown role code should not be possible");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void cannotReferenceUnknownPermission() {
        try {
            Metadata p = new Metadata(domain, system, system);
            p.addDelegatablePermission(TestData.roleCode, "Unknown", "Unknown", true);
            manager.putMetadata(p);

            fail("Saving metadata with delegatable permission having unknown permission code should not be possible");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void cannotGetUnknownMetadata() {
        try {
            manager.getMetadata("Unknown", "Unknown");

            fail("Loading unknown metadata should not be possible");
        } catch (IllegalArgumentException expectedException) {
        }
    }
}
