package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.SoapHeader;

import javax.xml.datatype.*;
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

    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

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
    public void canCreateDelegationAsDelegator() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        when(delegationManager.createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, state, permissionIds, null, null)).thenReturn(delegation);
        setupDgwsRequestContextForUser(delegatorCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemId);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleId);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionIds);
                setListOfPermissionIds(pIds);
                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
            }});
        }};

        final CreateDelegationsResponse response = service.createDelegations(request, soapHeader);

        verify(delegationManager).createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, state, permissionIds, null, null);

        assertEquals(1, response.getDelegation().size());
        verify(typeMapper).toDelegationType(delegation);
    }

    @Test
    public void canCreateDelegationAsDelegatee() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, State.BESTILT, null);

        when(delegationManager.createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, State.BESTILT, permissionIds, null, null)).thenReturn(delegation);
        setupDgwsRequestContextForUser(delegateeCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemId);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleId);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionIds);
                setListOfPermissionIds(pIds);
                setState(State.BESTILT);
            }});
        }};

        final CreateDelegationsResponse response = service.createDelegations(request, soapHeader);

        verify(delegationManager).createDelegation(systemId, delegatorCprText, delegateeCprText, delegateeCvrText, roleId, State.BESTILT, permissionIds, null, null);

        assertEquals(1, response.getDelegation().size());
        verify(typeMapper).toDelegationType(delegation);
    }

    @Test(expected = IllegalAccessError.class)
    public void cannotCreateDelegationForThirdPartyAsDelegator() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        setupDgwsRequestContextForUser(delegatorCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemId);
                setDelegatorCpr("anotherCpr");
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleId);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionIds);
                setListOfPermissionIds(pIds);
                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
            }});
        }};

        service.createDelegations(request, soapHeader);
    }

    @Test(expected = IllegalAccessError.class)
    public void cannotCreateDelegationWithStateGodkendtAsDelegatee() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        setupDgwsRequestContextForUser(delegateeCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemId);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleId);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionIds);
                setListOfPermissionIds(pIds);
                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
            }});
        }};

        service.createDelegations(request, soapHeader);
    }

    private Delegation createDelegation(final String id, State state, DateTime creationDate) {
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

        delegation.setState(state);
        delegation.setCreated(creationDate);
        delegation.setEffectiveFrom(now.minusDays(1));
        delegation.setEffectiveTo(now.plusDays(1));

        return delegation;
    }

    @Test
    public void canGetDelegationsByDelegatorCpr() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        when(delegationManager.getDelegationsByDelegatorCpr(delegatorCprText)).thenReturn(Arrays.asList(delegation));
        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegationsByDelegatorCpr(delegatorCprText);

        assertEquals(1, response.getDelegation().size());
    }

    @Test
    public void canGetDelegationsByDelegateeCpr() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        when(delegationManager.getDelegationsByDelegateeCpr(delegateeCprText)).thenReturn(Arrays.asList(delegation));
        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegateeCpr(delegateeCprText);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegationsByDelegateeCpr(delegateeCprText);

        assertEquals(1, response.getDelegation().size());
    }

    @Test
    public void canGetDelegationsById() throws Exception {
        final Delegation delegation = createDelegation(domainIdText, state, null);

        when(delegationManager.getDelegation(domainIdText)).thenReturn(delegation);
        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegationId(domainIdText);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegation(domainIdText);

        assertEquals(1, response.getDelegation().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetDelegationsByBadArguments() throws Exception {
        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            setDelegateeCpr(delegateeCprText);
        }};
        service.getDelegations(request, soapHeader);
    }


    @Test
    public void canDeleteDelegationsAsDelegator() throws Exception {
        when(delegationManager.deleteDelegation(delegatorCprText, null, domainIdText, now.plusDays(1))).thenReturn(domainIdText);
        setupDgwsRequestContextForUser(delegatorCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainIdText);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(delegatorCprText, null, domainIdText, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test
    public void canDeleteDelegationsAsDelegatee() throws Exception {
        when(delegationManager.deleteDelegation(null, delegateeCprText, domainIdText, now.plusDays(1))).thenReturn(domainIdText);
        setupDgwsRequestContextForUser(delegateeCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainIdText);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(null, delegateeCprText, domainIdText, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canDeleteDelegationsByDelegatorAndDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainIdText);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        service.deleteDelegations(request, soapHeader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetDelegationsByNoDelegatorNorDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainIdText);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        service.deleteDelegations(request, soapHeader);
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        if (dateTime == null)
            return null;
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }
}