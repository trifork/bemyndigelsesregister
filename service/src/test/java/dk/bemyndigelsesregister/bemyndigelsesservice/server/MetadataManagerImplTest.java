package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.DaoUnitTestSupport;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for MetadataManagerImpl
 */
public class MetadataManagerImplTest extends DaoUnitTestSupport {
    @Inject
    MetadataManager manager;

    @Inject
    EbeanServer ebeanServer;

    @Before
    public void setUp() {
        manager.clearCache();
    }

    @Test
    public void canClearExistingMetadata() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertNotNull(g);
            assertEquals(p.getDomainCode(), g.getDomainCode());
            assertEquals(p.getSystem().getCode(), g.getSystem().getCode());
            assertEquals(p.getSystem().getDescription(), g.getSystem().getDescription());
            assertEquals(0, g.getRoles().size());
            assertEquals(0, g.getPermissions().size());
            assertEquals(0, g.getDelegatablePermissions().size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void canChangeSystemDescription() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, "Test");
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertEquals(p.getSystem().getDescription(), g.getSystem().getDescription());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void canCreateNewMetadata() {
        try {
            ebeanServer.beginTransaction();

            String domain = "MyDomain";
            String system = "MySystem";

            Metadata p = new Metadata(domain, system, system);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(domain, system);

            assertNotNull(g);
            assertEquals(p.getDomainCode(), g.getDomainCode());
            assertEquals(p.getSystem().getCode(), g.getSystem().getCode());
            assertEquals(0, g.getRoles().size());
            assertEquals(0, g.getPermissions().size());
            assertEquals(0, g.getDelegatablePermissions().size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void canUpdateRoles() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addRole(TestData.roleCode, TestData.roleDescription);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertNotNull(g);
            assertEquals(1, g.getRoles().size());
            assertEquals(p.getRoles().get(0).getCode(), g.getRoles().get(0).getCode());
            assertEquals(p.getRoles().get(0).getDescription(), g.getRoles().get(0).getDescription());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void canUpdatePermissions() {
        try {
            ebeanServer.beginTransaction();

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
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void canUpdateDelegatablePermission() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addRole(TestData.roleCode, TestData.roleDescription);
            p.addPermission(TestData.permissionCode1, TestData.permissionDescription1);
            p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1, TestData.permissionDescription1, true);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertNotNull(g);
            assertEquals(1, g.getDelegatablePermissions().size());
            assertEquals(p.getDelegatablePermissions().get(0).getRoleCode(), g.getDelegatablePermissions().get(0).getRoleCode());
            assertEquals(p.getDelegatablePermissions().get(0).getPermissionCode(), g.getDelegatablePermissions().get(0).getPermissionCode());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotReferenceUnknownRole() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addDelegatablePermission("Unknown", TestData.permissionCode1, TestData.permissionDescription1, true);
            manager.putMetadata(p);
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotReferenceUnknownPermission() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addDelegatablePermission(TestData.roleCode, "Unknown", "Unknown", true);
            manager.putMetadata(p);
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGetUnknownMetadata() {
        manager.getMetadata("Unknown", "Unknown");
    }
}
