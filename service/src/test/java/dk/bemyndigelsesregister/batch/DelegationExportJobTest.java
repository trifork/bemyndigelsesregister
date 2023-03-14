package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.service.DelegationManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DelegationExportJobTest {

    @Autowired
    private DelegationExportJob job;

    @Autowired
    private DelegationManager delegationManager;

    @Test
    public void willExportDelegations() {
        Instant startTime = Instant.now();
        int delegationCount = 50;

        List<String> permissions = Arrays.asList(TestData.permissionCode1, TestData.permissionCode2);

        List<Long> delegationIds = new LinkedList<>();
        for (int n = 0; n < delegationCount; n++) {
            Delegation delegation = delegationManager.createDelegation(TestData.systemCode, generateCpr(1000 + n), generateCpr(2000 + n), "3333333", TestData.roleCode, Status.GODKENDT, permissions, null, null);
            delegationIds.add(delegation.getId());
        }

        int count = job.exportChangedDelegations(startTime, delegationIds);

        assertEquals(delegationCount * permissions.size(), count); // each BEM delegation has 2 permissions, which are exported individually
    }

    private String generateCpr(int n) {
        return "010203" + n;
    }
}
