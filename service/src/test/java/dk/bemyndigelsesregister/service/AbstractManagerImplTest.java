package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

@SpringBootTest
public abstract class AbstractManagerImplTest {

    // testdata
    protected final Instant date0 = DateUtils.toInstant(2014, 1, 1);
    protected final Instant date1 = DateUtils.toInstant(2015, 1, 1);
    protected final Instant date2 = DateUtils.toInstant(2015, 1, 10);
    protected final Instant date3 = DateUtils.toInstant(2016, 1, 10);
    protected final String CVR_NUMBER = "3333333333";

    protected static int cprGenerator = 1000000000;

    @Autowired
    protected DelegationManager delegationManager;

    protected Delegation createDelegation(String delegatorCpr, String delegateeCpr, String delegateeCvr, Status state, List<String> permissions, Instant effectiveFrom, Instant effectiveTo) {
        return delegationManager.createDelegation(TestData.systemCode, delegatorCpr, delegateeCpr, delegateeCvr, TestData.roleCode, state, permissions, effectiveFrom, effectiveTo);
    }

    protected String generateCpr() {
        return String.valueOf(cprGenerator++);
    }
}
