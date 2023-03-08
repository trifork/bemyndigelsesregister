package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.Metadata;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DelegationManagerImplTest {
    @Autowired
    private DelegationManager manager;

    private final Instant date0 = DateUtils.toInstant(2014, 1, 1);
    private final Instant date1 = DateUtils.toInstant(2015, 1, 1);
    private final Instant date2 = DateUtils.toInstant(2015, 1, 10);
    private final Instant date3 = DateUtils.toInstant(2016, 1, 10);
    private final String CVR_NUMBER = "3333333333";

    private static int cprGenerator = 1000000000;

    @Test
    public void testCreateOverlappingDelegation() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        // create delegation valid from date1
        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);

        // create delegation valid from date2
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);

        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testCreateOverlappingDelegationWithoutCVR() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        // create delegation valid from date1
        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, null, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);

        // create delegation valid from date2
        createDelegation(delegatorCpr, delegateeCpr, null, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);

        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testCreateApprovedClosesOverlappingRequested() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testCreateApprovedClosesOverlappingApproved() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testCreateRequestedDoesNotCloseOverlappingApproved() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation
        Instant effectiveTo = delegation.getEffectiveTo();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
        delegation = manager.getDelegation(delegation.getCode()); // reload first delegation

        assertEquals(effectiveTo, delegation.getEffectiveTo());
    }

    @Test
    public void testCreateDelegationWithAsteriskPermission() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(Metadata.ASTERISK_PERMISSION_CODE, TestData.permissionCode1), date1, null);
        delegation = manager.getDelegation(delegation.getCode()); // reload delegation

        assertNotNull(delegation);
        assertNotNull(delegation.getDelegationPermissions());
        assertEquals(3, delegation.getDelegationPermissions().size());
    }

    @Test
    public void testDeleteDelegationAsDelegator() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        String uuid = manager.deleteDelegation(delegatorCpr, null, delegation.getCode(), date2);
        delegation = manager.getDelegation(uuid); // reload delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testDeleteDelegationAsDelegatee() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        String uuid = manager.deleteDelegation(null, delegateeCpr, delegation.getCode(), date2);
        delegation = manager.getDelegation(uuid); // reload delegation

        assertEquals(date2, delegation.getEffectiveTo());
    }

    @Test
    public void testDeleteNonExistingDelegation() {
        String delegateeCpr = generateCpr();

        String uuid = manager.deleteDelegation(null, delegateeCpr, "non-existing-delegation-id", date2);

        assertNull(uuid);
    }

    @Test
    public void testDeleteThirdPartyDelegation() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        Delegation delegation = createDelegation("another", delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        String delegationId = delegation.getCode();

        Delegation loadedDelegation = manager.getDelegation(delegationId); // reload delegation

        String uuid = manager.deleteDelegation(delegatorCpr, null, delegationId, date2);
        assertNull(uuid);

        Delegation reloadedDelegation = manager.getDelegation(delegationId); // reload delegation

        assertEquals(loadedDelegation.getCode(), reloadedDelegation.getCode());
        assertEquals(loadedDelegation.getEffectiveTo(), reloadedDelegation.getEffectiveTo());
    }

    @Test
    public void testDeleteDelegationBadDate() {
        try {
            String delegatorCpr = generateCpr();
            String delegateeCpr = generateCpr();

            Delegation delegation = createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
            manager.deleteDelegation(delegatorCpr, null, delegation.getCode(), date2); // should fail because date2 is after date1

            fail("Create delegation with wrong date order should not be possible");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void testFindDelegationByDelegator() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void testFindDelegationByDelegatorAndFromDate() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        List<Delegation> before = manager.getDelegationsByDelegatorCpr(delegatorCpr, date1, null);

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, date2);
        List<Delegation> after = manager.getDelegationsByDelegatorCpr(delegatorCpr, date1, null);

        assertNotNull(after);
        assertEquals(before.size() + 1, after.size());
    }

    @Test
    public void testFindDelegationByDelegatorAndToDate() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, date3);
        List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr, null, date1);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    public void testFindDelegationByDelegatorAndPeriod() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date0, date1);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.GODKENDT, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, date3);
        List<Delegation> list = manager.getDelegationsByDelegatorCpr(delegatorCpr, date0, date3);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void testFindDelegationByDelegatee() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date2, null);
        List<Delegation> list = manager.getDelegationsByDelegateeCpr(delegateeCpr);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void testCleanupDelegations() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();
        Instant oldDate = DateUtils.toInstant(1990, 1, 1);

        int n = manager.getDelegationsByDelegatorCpr(delegatorCpr).size();
        for (int i = 0; i < 10; i++) {
            createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), Instant.now(), DateUtils.plusDays(Instant.now(), 2));
            createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), oldDate, DateUtils.plusDays(oldDate, 2));
        }
        assertEquals(n + 20, manager.getDelegationsByDelegatorCpr(delegatorCpr).size());

        Instant cleanupDate = DateUtils.toInstant(1991, 1, 1);
        int cleanCount = manager.cleanup(cleanupDate, 4);
        assertEquals(4, cleanCount);

        cleanCount = manager.cleanup(cleanupDate, 100);
        assertEquals(6, cleanCount);

        assertEquals(n + 10, manager.getDelegationsByDelegatorCpr(delegatorCpr).size());
    }

    private Delegation createDelegation(String delegatorCpr, String delegateeCpr, String delegateeCvr, Status state, List<String> permissions, Instant effectiveFrom, Instant effectiveTo) {
        return manager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, state, permissions, effectiveFrom, effectiveTo);
    }
   
    private String generateCpr() {
        return String.valueOf(cprGenerator++);
    }
}
