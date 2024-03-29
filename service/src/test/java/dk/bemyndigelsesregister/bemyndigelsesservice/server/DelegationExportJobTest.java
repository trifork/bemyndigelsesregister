package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegatingSystemDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.SystemVariableDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel.Delegations;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DelegationExportJobTest {
    DelegationExportJob job = new DelegationExportJob();
    DelegationDao delegationDao = Mockito.mock(DelegationDao.class);
    DelegatingSystemDao delegatingSystemDao = Mockito.mock(DelegatingSystemDao.class);
    private final SystemVariableDao systemVariableDao = Mockito.mock(SystemVariableDao.class);

    NspManager nspManager = Mockito.mock(NspManager.class);
    private final SystemService systemService = Mockito.mock(SystemService.class);

    @Before
    public void setUp() {
        job.nspManager = nspManager;
        job.delegationDao = delegationDao;
        job.systemVariableDao = systemVariableDao;
        job.systemService = systemService;
        job.jobEnabled = "true";
        job.batchSize = 5000;
        job.retentionDays = 180;
    }

    @Test
    public void willSendBemyndigelserToNsp() throws Exception {
        final DateTime startTime = new DateTime();
        final Long id1 = 1L, id2 = 2L;
        final Delegation delegation1 = createDelegation("bemyndigede cpr 1"), delegation2 = createDelegation("bemyndigede cpr 2");
        final DelegatingSystem system = createSystem("system1");
        final SystemVariable lastRunSV = new SystemVariable("lastRun", new DateTime(0l));

        DateTime fromIncluding = lastRunSV.getDateTimeValue();
        fromIncluding = fromIncluding.minusMinutes(1);

        DateTime toExcluding = startTime;
        toExcluding = toExcluding.minusMinutes(1);

        when(systemVariableDao.getByName("lastRun")).thenReturn(lastRunSV);
        when(systemService.getDateTime()).thenReturn(startTime);
        when(delegatingSystemDao.findByLastModifiedGreaterThanOrEquals(lastRunSV.getDateTimeValue())).thenReturn(Arrays.asList(system));
        when(delegationDao.findByModifiedInPeriod(fromIncluding, toExcluding)).thenReturn(Arrays.asList(id1, id2));
        when(delegationDao.get(id1)).thenReturn(delegation1);
        when(delegationDao.get(id2)).thenReturn(delegation2);

        job.startExport();

        verify(nspManager).send(bemyndigelserEq(Arrays.asList(delegation1, delegation2)), eq(startTime), eq(1));
        verify(systemVariableDao).save(lastRunSV);
    }

    @Test
    public void canRunCompleteExport() {
        final DateTime startTime = new DateTime();
        final Long id1 = 1L, id2 = 2L;
        final Delegation delegation1 = createDelegation("bemyndigede cpr 1"), delegation2 = createDelegation("bemyndigede cpr 2");
        final SystemVariable lastRunSV = new SystemVariable("lastRun", new DateTime(0l));

        when(systemVariableDao.getByName("lastRun")).thenReturn(lastRunSV);
        when(systemService.getDateTime()).thenReturn(startTime);
        when(delegationDao.findByModifiedInPeriod(null, null)).thenReturn(Arrays.asList(id1, id2));
        when(delegationDao.get(id1)).thenReturn(delegation1);
        when(delegationDao.get(id2)).thenReturn(delegation2);

        job.completeExport();

        verify(nspManager).send(bemyndigelserEq(Arrays.asList(delegation1, delegation2)), eq(startTime), eq(1));
        verify(systemVariableDao).save(lastRunSV);
    }

    private DelegatingSystem createSystem(String name) {
        final DelegatingSystem system = new DelegatingSystem();
        system.setCode(name);
        return system;
    }

    private Delegation createDelegation(String delegateeCpr) {
        final Delegation delegation = new Delegation();

        delegation.setState(Status.GODKENDT);
        delegation.setDelegateeCpr(delegateeCpr);

        final DelegationPermission permission = new DelegationPermission();
        permission.setPermissionCode("TEST permission");
        Set<DelegationPermission> permissions = new HashSet<>();
        permissions.add(permission);
        delegation.setDelegationPermissions(permissions);

        delegation.setRoleCode("Test arbejdsfunktion");
        delegation.setSystemCode("TEST system");
        delegation.setLastModified(new DateTime());
        delegation.setLastModifiedBy("Test");

        return delegation;
    }

    private Delegations bemyndigelserEq(final List<Delegation> delegations) {
        System.out.println("Cheking bemyndigelser");
        return argThat(new TypeSafeMatcher<Delegations>() {
            @Override
            public boolean matchesSafely(Delegations item) {
                if (item.getRecordCount() != delegations.size()) {
                    System.out.println("Not same size");
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        });
    }
}
