package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Metadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MetadataManagerImplTest {
    @Autowired
    MetadataManager manager;

    @Test
    public void canClearExistingMetadata() {
        String domain = "MyExampleDomain";
        String system = "MyExampleSystem";

        Metadata p = new Metadata(domain, system, system);

        p.addRole("MyRoleCode", "MyRoleDescription");
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

        Metadata p = manager.getMetadata(TestData.domainCode, TestData.systemCode);
        p.getSystem().setDescription(newDescription);
        manager.putMetadata(p);

        Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

        assertEquals(newDescription, g.getSystem().getDescription());
    }

    @Test
    public void canCreateNewMetadata() {
        String domain = "MyDomain";
        String system = "MySystem";

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
        Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
        p.addRole(TestData.roleCode, TestData.roleDescription);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

        assertNotNull(g);
        assertEquals(1, g.getRoles().size());
        assertEquals(p.getRoles().get(0).getCode(), g.getRoles().get(0).getCode());
        assertEquals(p.getRoles().get(0).getDescription(), g.getRoles().get(0).getDescription());
    }

    @Test
    public void canUpdatePermissions() {
        Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addPermission(TestData.permissionCode2, TestData.permissionDescription2);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

        assertNotNull(g);
        assertEquals(2, g.getPermissions().size());
        for (int i = 0; i < 2; i++) {
            assertEquals(p.getPermissions().get(i).getCode(), g.getPermissions().get(i).getCode());
            assertEquals(p.getPermissions().get(i).getDescription(), g.getPermissions().get(i).getDescription());
        }
    }

    @Test
    public void canUpdateDelegatablePermission() {
        Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
        p.addRole(TestData.roleCode, TestData.roleDescription);
        p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
        p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
        manager.putMetadata(p);
        Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

        assertNotNull(g);
        assertEquals(1, g.getDelegatablePermissions().size());
        assertEquals(p.getDelegatablePermissions().get(0).getRole().getCode(), g.getDelegatablePermissions().get(0).getRole().getCode());
        assertEquals(p.getDelegatablePermissions().get(0).getPermission().getCode(), g.getDelegatablePermissions().get(0).getPermission().getCode());
    }

    @Test
    public void cannotReferenceUnknownRole() {
        try {
            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addDelegatablePermission("Unknown", TestData.permissionCode1, TestData.permissionDescription1, true);
            manager.putMetadata(p);

            fail("Saving metadata with delegatable permission having unknown role code should not be possible");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void cannotReferenceUnknownPermission() {
        try {
            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
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
