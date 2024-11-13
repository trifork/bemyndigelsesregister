package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DumpRestoreManagerImplTest extends AbstractManagerImplTest {
    @Autowired
    private DumpRestoreManager dumpRestoreManager;

    @Test
    public void testResetPatients() {
        String delegatorCpr = generateCpr();
        String delegateeCpr = generateCpr();

        createDelegation(delegatorCpr, delegateeCpr, CVR_NUMBER, Status.ANMODET, Arrays.asList(TestData.permissionCode1, TestData.permissionCode2), date1, null);
        List<Delegation> delegations = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr);
        assertFalse(delegations.isEmpty());

        List<String> result = dumpRestoreManager.resetPatients(Collections.singletonList(delegatorCpr));
        assertEquals(1, result.size());
        assertEquals(delegatorCpr, result.get(0));

        delegations = delegationManager.getDelegationsByDelegatorCpr(delegatorCpr);
        assertTrue(delegations.isEmpty());
    }
}
