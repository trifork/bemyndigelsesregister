package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * BEM 2.0 rettighed til bemyndigelse
 * Created by obj on 02-02-2016.
 */
public class DelegationDaoEbeanTest extends DaoUnitTestSupport {
    @Inject
    DelegationDao dao;

    @Inject
    EbeanServer ebeanServer;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Test
    public void testCreateDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            int n = dao.list().size();

            Delegation d = new Delegation();
            d.setCode("testId1");
            d.setDelegatorCpr("0101010AB1");
            d.setDelegateeCpr("0202020AB2");
            d.setDelegateeCvr("12345678");
            d.setEffectiveFrom(new DateTime(System.currentTimeMillis()));
            d.setEffectiveTo(new DateTime(System.currentTimeMillis() + 20000000));
            d.setSystemCode(systemDao.get(1).getCode());
            d.setRoleCode(roleDao.get(1).getCode());
            d.setState(Status.GODKENDT);

            Set<DelegationPermission> permissions = new HashSet<>();

            Permission p = permissionDao.get(1);
            DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(d);
            permission.setPermissionCode(p.getCode());
            permission.setCode("testId2");
            permissions.add(permission);

            d.setDelegationPermissions(permissions);

            dao.save(d);

            assertEquals("After save the no. of delegations should increase by one", n + 1, dao.list().size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testCreateDelegationWithTwoPermissions() throws Exception {
        final String testCode = "testCode1";

        try {
            ebeanServer.beginTransaction();

            Delegation d = new Delegation();
            d.setCode(testCode);
            d.setDelegatorCpr("0101010AB1");
            d.setDelegateeCpr("0202020AB2");
            d.setDelegateeCvr("12345678");
            d.setEffectiveFrom(new DateTime(System.currentTimeMillis()));
            d.setEffectiveTo(new DateTime(System.currentTimeMillis() + 20000000));
            d.setSystemCode(systemDao.get(1).getCode());
            d.setRoleCode(roleDao.get(1).getCode());
            d.setState(Status.GODKENDT);

            Set<DelegationPermission> permissions = new HashSet<>();

            Permission p1 = permissionDao.get(1);
            DelegationPermission delegationPermission1 = new DelegationPermission();
            delegationPermission1.setDelegation(d);
            delegationPermission1.setPermissionCode(p1.getCode());
            delegationPermission1.setCode("testCode2");
            permissions.add(delegationPermission1);

            Permission p2 = permissionDao.get(2);
            DelegationPermission delegationPermission2 = new DelegationPermission();
            delegationPermission2.setDelegation(d);
            delegationPermission2.setPermissionCode(p2.getCode());
            delegationPermission2.setCode("testCode3");
            permissions.add(delegationPermission2);

            d.setDelegationPermissions(permissions);

            dao.save(d);

            Delegation retrievedDelegation = dao.findByCode(testCode);
            assertNotNull(retrievedDelegation.getDelegationPermissions());
            assertEquals("Delegation should contain the right number of permissions", 2, retrievedDelegation.getDelegationPermissions().size());
            for (DelegationPermission dp : retrievedDelegation.getDelegationPermissions()) {
                assertTrue("Delegation should contain the right permissions", dp.getPermissionCode().equals(delegationPermission1.getPermissionCode())
                                                                || dp.getPermissionCode().equals(delegationPermission2.getPermissionCode()));
            }
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testUpdateDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = dao.get(1);
            delegation.setLastModifiedBy("TestCase was here: " + System.currentTimeMillis());
//            dao.save(delegation);

            delegation = dao.get(1);
            assertTrue("Text \"" + delegation.getLastModifiedBy() + "\" should start with \"TestCase was here\"", delegation.getLastModifiedBy().startsWith("TestCase was here"));
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testGetDelegation() throws Exception {
        Delegation delegation = dao.get(1);
        System.out.println(delegation);
        assertNotNull(delegation);
    }

    @Test
    public void testListDelegations() throws Exception {
        final List<Delegation> delegations = dao.list();

        assertEquals("Unexpected no. of delegations found", 5, delegations.size());

        System.out.println(delegations);
    }

    @Test
    public void testFindByDelegator() throws Exception {
        final List<Delegation> delegations = dao.findByDelegatorCpr("1010101010", null, null);

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());
    }

    @Test
    public void testFindByDelegatorAndFromDate() throws Exception {
        final List<Delegation> delegations = dao.findByDelegatorCpr("1010101010", new DateTime(2011, 5, 22, 0, 0), null);

        assertEquals("Unexpected no. of delegations found", 1, delegations.size());
    }

    @Test
    public void testFindByDelegatorAndToDate() throws Exception {
        final List<Delegation> delegations = dao.findByDelegatorCpr("1010101010", null, new DateTime(2011, 5, 22, 0, 0));

        assertEquals("Unexpected no. of delegations found", 1, delegations.size());
    }

    @Test
    public void testFindByDelegatee() throws Exception {
        final List<Delegation> delegations = dao.findByDelegateeCpr("1010101012", null, null);

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());
    }

    @Test
    public void testFindByCodes() throws Exception {
        final List<Delegation> delegations = dao.findByCodes(Arrays.asList("TestKode1", "TestKode3"));

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());
    }

    @Test
    public void testFindByLastModifiedGreaterThanOrEquals() throws Exception {
        final List<Long> delegations = dao.findByModifiedInPeriod(new DateTime(2011, 1, 1, 0, 0), null);

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());
    }

    @Test
    public void testFindWithAsterisk() throws Exception {
        final List<Long> delegationIds = dao.findWithAsterisk("FMK", DateTime.now());

        System.out.println(delegationIds);
    }

    @Test
    public void testCleanupDelegations() {
        try {
            ebeanServer.beginTransaction();

            final DateTime oldDate = new DateTime(1990, 1, 1, 10, 0, 0);

            int n = dao.list().size();
            for (int i = 0; i < 10; i++) {
                Delegation d = createDelegation(new DateTime(System.currentTimeMillis()), new DateTime(System.currentTimeMillis() + 20000000));
                dao.save(d);
                d = createDelegation(oldDate, oldDate);
                dao.save(d);
            }
            assertEquals("After save the no. of delegations should increase by 20", n + 20, dao.list().size());

            final DateTime cleanupDate = new DateTime(1991, 1, 1, 10, 0, 0);
            int cleanCount = dao.cleanup(cleanupDate, 4);
            assertEquals("Cleanup should delete 4 old delegations", 4, cleanCount);

            cleanCount = dao.cleanup(cleanupDate, 100);
            assertEquals("Cleanup should delete 6 old delegations", 6, cleanCount);

            assertEquals("After clean the no. of delegations should be decreased by 10", n + 10, dao.list().size());
        } finally {
            ebeanServer.endTransaction();
        }
    }

    private Delegation createDelegation(DateTime effectiveFrom, DateTime effectiveTo) {
        Delegation d = new Delegation();
        d.setCode("testId1");
        d.setDelegatorCpr("0101010AB1");
        d.setDelegateeCpr("0202020AB2");
        d.setDelegateeCvr("12345678");
        d.setEffectiveFrom(effectiveFrom);
        d.setEffectiveTo(effectiveTo);
        d.setSystemCode(systemDao.get(1).getCode());
        d.setRoleCode(roleDao.get(1).getCode());
        d.setState(Status.GODKENDT);

        Set<DelegationPermission> permissions = new HashSet<>();

        Permission p1 = permissionDao.get(1);
        DelegationPermission delegationPermission1 = new DelegationPermission();
        delegationPermission1.setDelegation(d);
        delegationPermission1.setPermissionCode(p1.getCode());
        delegationPermission1.setCode("testCode2");
        permissions.add(delegationPermission1);

        Permission p2 = permissionDao.get(2);
        DelegationPermission delegationPermission2 = new DelegationPermission();
        delegationPermission2.setDelegation(d);
        delegationPermission2.setPermissionCode(p2.getCode());
        delegationPermission2.setCode("testCode3");
        permissions.add(delegationPermission2);

        d.setDelegationPermissions(permissions);

        return d;
    }

}
