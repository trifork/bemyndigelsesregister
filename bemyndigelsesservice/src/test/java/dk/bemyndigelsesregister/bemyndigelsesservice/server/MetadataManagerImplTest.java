package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean.DaoUnitTestSupport;
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

    @Test
    public void canClearExistingMetadata() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertNotNull(g);
            assertEquals(g.getDomainId(), p.getDomainId());
            assertEquals(g.getSystem().getDomainId(), p.getSystem().getDomainId());
            assertEquals(g.getSystem().getDescription(), p.getSystem().getDescription());
            assertEquals(g.getRoles().size(), 0);
            assertEquals(g.getPermissions().size(), 0);
            assertEquals(g.getDelegatablePermissions().size(), 0);
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

            assertEquals(g.getSystem().getDescription(), p.getSystem().getDescription());
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
            assertEquals(g.getDomainId(), p.getDomainId());
            assertEquals(g.getSystem().getDomainId(), p.getSystem().getDomainId());
            assertEquals(g.getRoles().size(), 0);
            assertEquals(g.getPermissions().size(), 0);
            assertEquals(g.getDelegatablePermissions().size(), 0);
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
            assertEquals(g.getRoles().size(), 1);
            assertEquals(g.getRoles().get(0).getDomainId(), p.getRoles().get(0).getDomainId());
            assertEquals(g.getRoles().get(0).getDescription(), p.getRoles().get(0).getDescription());
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
            assertEquals(g.getPermissions().size(), 2);
            for (int i = 0; i < 2; i++) {
                assertEquals(g.getPermissions().get(i).getDomainId(), p.getPermissions().get(i).getDomainId());
                assertEquals(g.getPermissions().get(i).getDescription(), p.getPermissions().get(i).getDescription());
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
            p.addDelegatablePermission(TestData.roleCode, TestData.permissionCode1);
            manager.putMetadata(p);
            Metadata g = manager.getMetadata(TestData.domainCode, TestData.systemCode);

            assertNotNull(g);
            assertEquals(g.getDelegatablePermissions().size(), 1);
            assertEquals(g.getDelegatablePermissions().get(0).getRoleId(), p.getDelegatablePermissions().get(0).getRoleId());
            assertEquals(g.getDelegatablePermissions().get(0).getPermissionId(), p.getDelegatablePermissions().get(0).getPermissionId());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotReferenceUnknownRole() {
        try {
            ebeanServer.beginTransaction();

            Metadata p = new Metadata(TestData.domainCode, TestData.systemCode, TestData.systemDescription);
            p.addDelegatablePermission("Unknown", TestData.permissionCode1);
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
            p.addDelegatablePermission(TestData.roleCode, "Unknown");
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
