package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.DelegationPermission;
import dk.bemyndigelsesregister.domain.Permission;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DelegationDAOTest {
    @Autowired
    private DelegatingSystemDAO delegatingSystemDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private DelegationDAO delegationDAO;

    @Test
    public void testCreateDelegation() {
        int n = delegationDAO.list().size();

        Delegation d = new Delegation();
        d.setCode("testId1");
        d.setDelegatorCpr("0101010AB1");
        d.setDelegateeCpr("0202020AB2");
        d.setDelegateeCvr("12345678");
        d.setEffectiveFrom(Instant.now());
        d.setEffectiveTo(DateUtils.plusDays(Instant.now(), 2));
        d.setSystemCode(delegatingSystemDAO.get(1).getCode());
        d.setRoleCode(roleDAO.get(1).getCode());
        d.setState(Status.GODKENDT);

        Set<DelegationPermission> permissions = new HashSet<>();

        Permission p = permissionDAO.get(1);
        DelegationPermission permission = new DelegationPermission();
        permission.setDelegationId(d.getId());
        permission.setPermissionCode(p.getCode());
        permission.setCode("testId2");
        permissions.add(permission);

        d.setDelegationPermissions(permissions);

        delegationDAO.save(d);

        assertEquals(n + 1, delegationDAO.list().size());
    }

    @Test
    public void testUpdateDelegation() {
        Instant effectiveFrom = Instant.now();
        Instant effectiveTo = DateUtils.plusDays(Instant.now(), 2);
        Delegation delegation = createDelegation(effectiveFrom, effectiveTo);
        delegation.setCode(UUID.randomUUID().toString()); // use a unique code
        delegationDAO.save(delegation);

        // modify
        delegation.setEffectiveTo(DateUtils.plusDays(Instant.now(), 6));
        delegationDAO.save(delegation);

        Delegation modifiedDelegation = delegationDAO.findByCode(delegation.getCode());
        assertTrue(modifiedDelegation.getEffectiveTo().isAfter(effectiveTo));
    }

    @Test
    public void testGetDelegation() {
        Delegation delegation = delegationDAO.get(1);
        System.out.println(delegation);
        assertNotNull(delegation);
    }

    @Test
    public void testListDelegations() {
        final List<Delegation> delegations = delegationDAO.list();

        assertTrue(delegations.size() > 2);
        System.out.println(delegations);
    }

    @Test
    public void testFindByDelegator() {
        final List<Delegation> delegations = delegationDAO.findByDelegatorCpr("1010101010", null, null);

        assertEquals(2, delegations.size());
    }

    @Test
    public void testFindByDelegatorAndFromDate() {
        final List<Delegation> delegations = delegationDAO.findByDelegatorCpr("1010101010", DateUtils.toInstant(2011, 5, 22), null);

        assertEquals(1, delegations.size());
    }

    @Test
    public void testFindByDelegatorAndToDate() {
        final List<Delegation> delegations = delegationDAO.findByDelegatorCpr("1010101010", null, DateUtils.toInstant(2011, 5, 22));

        assertEquals(1, delegations.size());
    }

    @Test
    public void testFindByDelegatee() {
        final List<Delegation> delegations = delegationDAO.findByDelegateeCpr("1010101012", null, null);

        assertEquals(2, delegations.size());
    }

    @Test
    public void testFindByCodes() {
        final List<Delegation> delegations = delegationDAO.findByCodes(Arrays.asList("TestKode1", "TestKode3"));

        assertEquals(2, delegations.size());
    }

    @Test
    public void testFindByLastModifiedGreaterThanOrEquals() {
        Instant when = DateUtils.toInstant(2011, 1, 1);

        final List<Long> before = delegationDAO.findByModifiedInPeriod(when, null);

        // modify one
        Delegation delegation = createDelegation(Instant.now(), DateUtils.plusDays(Instant.now(), 2));
        delegationDAO.save(delegation);

        final List<Long> after = delegationDAO.findByModifiedInPeriod(when, null);

        assertEquals(before.size() + 1, after.size());
    }

    @Test
    public void testFindWithAsterisk() {
        final List<Long> delegationIds = delegationDAO.findWithAsterisk("FMK", Instant.now());

        System.out.println(delegationIds);
    }

    private Delegation createDelegation(Instant effectiveFrom, Instant effectiveTo) {
        Delegation d = new Delegation();
        d.setCode("testId1");
        d.setDelegatorCpr("0101010AB1");
        d.setDelegateeCpr("0202020AB2");
        d.setDelegateeCvr("12345678");
        d.setEffectiveFrom(effectiveFrom);
        d.setEffectiveTo(effectiveTo);
        d.setSystemCode(delegatingSystemDAO.get(1).getCode());
        d.setRoleCode(roleDAO.get(1).getCode());
        d.setState(Status.GODKENDT);

        Set<DelegationPermission> permissions = new HashSet<>();

        Permission p1 = permissionDAO.get(1);
        DelegationPermission delegationPermission1 = new DelegationPermission();
        delegationPermission1.setDelegationId(d.getId());
        delegationPermission1.setPermissionCode(p1.getCode());
        delegationPermission1.setCode("testCode2");
        permissions.add(delegationPermission1);

        Permission p2 = permissionDAO.get(2);
        DelegationPermission delegationPermission2 = new DelegationPermission();
        delegationPermission2.setDelegationId(d.getId());
        delegationPermission2.setPermissionCode(p2.getCode());
        delegationPermission2.setCode("testCode3");
        permissions.add(delegationPermission2);

        d.setDelegationPermissions(permissions);

        return d;
    }
}
