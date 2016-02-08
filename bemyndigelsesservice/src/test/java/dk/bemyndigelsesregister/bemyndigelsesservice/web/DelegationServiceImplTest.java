package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.CreateDelegationsRequest;
import dk.nsi.bemyndigelse._2016._01._01.CreateDelegationsResponse;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.SoapHeader;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DelegationServiceImplTest {
    @InjectMocks
    BemyndigelsesServiceImpl service = new BemyndigelsesServiceImpl();

    @Mock
    DelegationManager delegationManager;
    @Mock
    DelegationDao delegationDao;
    @Mock
    DomainDao domainDao;
    @Mock
    DelegatingSystem delegatingSystem;
    @Mock
    RoleDao roleDao;
    @Mock
    PermissionDao permissionDao;
    @Mock
    DelegationPermissionDao delegationPermissionDao;
    @Mock
    SystemService systemService;
    @Mock
    DgwsRequestContext dgwsRequestContext;
    @Mock
    ServiceTypeMapper typeMapper;
    @Mock
    WhitelistChecker whitelistChecker;

    @Mock
    SoapHeader soapHeader;

    private final DateTime now = new DateTime();

    final String domainIdText = "UUID kode";
    final String delegatorCprText = "BemyndigendeCpr";
    final String delegateeCprText = "BemyndigedeCpr";
    final String delegateeCvrText = "BemyndigedeCvr";
    final String roleId = "Arbejdsfunktionskode";
    final String systemId = "SystemKode";
    final String permissionId1 = "P1";
    final String permissionId2 = "P2";
    final List<String> permissionIds = Arrays.asList(permissionId1, permissionId2);
    final State state = State.GODKENDT;

    void setupDgwsRequestContextForSystem(String cvr) {
        when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.SYSTEM, 3));
        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, cvr, null));
    }

    void setupDgwsRequestContextForUser(String cpr) {
        when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.USER, 4));
        when(dgwsRequestContext.getIdCardUserLog()).thenReturn(new IdCardUserLog(cpr, null, null, null, null, null, null));
    }

    @Test
    public void canCreateDelegation() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, null);

        when(delegationManager.createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, state, permissionIds, null, null)).thenReturn(delegation);
        setupDgwsRequestContextForUser("BemyndigendeCpr");

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemId);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleId);
                setListOfPermissionIds(new ListOfPermissionIds() {{
                    getPermissionId().addAll(permissionIds);
                }});
                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
            }});
        }};

        final CreateDelegationsResponse response = service.createDelegations(request, soapHeader);

        verify(delegationManager).createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, state, permissionIds, null, null);

        assertEquals(1, response.getDelegation().size());
        final dk.nsi.bemyndigelse._2016._01._01.Delegation responseDelegation = response.getDelegation().get(0);
        assertEquals(domainIdText, responseDelegation.getDelegationId());
        assertEquals(delegatorCprText, responseDelegation.getDelegatorCpr());
        assertEquals(delegateeCprText, responseDelegation.getDelegateeCpr());
        assertEquals(roleId, responseDelegation.getRole().getRoleId());
//        assertEquals(permissionId1, responseDelegation.getPermission().get(0).getPermissionId()); TODO OBJ fix - responseDelegation.getPermission() returnerer null
//        assertEquals(permissionId2, responseDelegation.getPermission().get(1).getPermissionId());
        assertEquals(systemId, responseDelegation.getSystem().getSystemId());
    }


    private Delegation createDelegation(final String id, DateTime creationDate) {
        final Delegation delegation = new Delegation();

        delegation.setDomainId(id);

        delegation.setDelegatorCpr(delegatorCprText);
        delegation.setDelegateeCpr(delegateeCprText);
        delegation.setDelegateeCvr(delegateeCvrText);
        delegation.setDelegatingSystem(this.systemId);
        delegation.setRole(this.roleId);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionIds) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(delegation);
            permission.setPermissionId(permissionId);
            permissionList.add(permission);
        }
        delegation.setDelegationPermissions(permissionList);

        delegation.setState(this.state);
        delegation.setCreated(creationDate);
        delegation.setEffectiveFrom(now.minusDays(1));
        delegation.setEffectiveTo(now.plusDays(1));

        return delegation;
    }


    public boolean allTrue(boolean... eval) {
        int a = 0;
        for (boolean b : eval) {
            if (!b) {
                System.out.println("Arg " + a + " was not true");
                return false;
            }
            a++;
        }
        return true;
    }
}
