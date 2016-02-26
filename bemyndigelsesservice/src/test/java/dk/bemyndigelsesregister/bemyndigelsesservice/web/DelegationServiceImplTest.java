package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import com.trifork.dgws.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Metadata;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import dk.bemyndigelsesregister.shared.service.SystemService;
import dk.nsi.bemyndigelse._2016._01._01.*;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.soap.SoapHeader;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

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
    MetadataManager metadataManager;

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

    final String domainId = "UUID kode";
    final String delegatorCprText = "DelegatorCpr";
    final String delegateeCprText = "DelegateeCpr";
    final String delegateeCvrText = "DelegateeCvr";
    final String roleId = "RoleId";
    final String roleDescription = "RoleDescription";
    final String systemId = "SystemId";
    final String systemDescription = "SystemDescription";
    final String permissionId = "PermissionId";
    final String permissionDescription = "PermissionDescription;";
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
        final Delegation delegation = createDelegation(domainId, state, null);

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
        final Delegation delegation = createDelegation(domainId, State.BESTILT, null);

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
        final Delegation delegation = createDelegation(domainId, state, null);

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
        final Delegation delegation = createDelegation(domainId, state, null);

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
        final Delegation delegation = createDelegation(domainId, state, null);

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
        final Delegation delegation = createDelegation(domainId, state, null);

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
        final Delegation delegation = createDelegation(domainId, state, null);

        when(delegationManager.getDelegation(domainId)).thenReturn(delegation);
        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegationId(domainId);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegation(domainId);

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
        when(delegationManager.deleteDelegation(delegatorCprText, null, domainId, now.plusDays(1))).thenReturn(domainId);
        setupDgwsRequestContextForUser(delegatorCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainId);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(delegatorCprText, null, domainId, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test
    public void canDeleteDelegationsAsDelegatee() throws Exception {
        when(delegationManager.deleteDelegation(null, delegateeCprText, domainId, now.plusDays(1))).thenReturn(domainId);
        setupDgwsRequestContextForUser(delegateeCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainId);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(null, delegateeCprText, domainId, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canDeleteDelegationsByDelegatorAndDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainId);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        service.deleteDelegations(request, soapHeader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetDelegationsByNoDelegatorNorDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(domainId);
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

    @Test
    public void canGetMetadata() throws Exception {
        GetMetadataRequest request = new GetMetadataRequest();
        request.setDomain(domainId);
        request.setSystemId(systemId);

        Metadata metadata = new Metadata(domainId, systemId, systemDescription);
        metadata.addRole(roleId, roleDescription);
        metadata.addPermission(permissionId, permissionDescription);
        metadata.addDelegatablePermission(roleId, permissionId);

        when(metadataManager.getMetadata(domainId, systemId)).thenReturn(metadata);

        GetMetadataResponse response = service.getMetadata(request, soapHeader);

        assertEquals(response.getDomain(), domainId);
        assertEquals(response.getSystem().getSystemId(), systemId);
        assertEquals(response.getSystem().getSystemLongName(), systemDescription);
        assertEquals(response.getRole().get(0).getRoleId(), roleId);
        assertEquals(response.getRole().get(0).getRoleDescription(), roleDescription);
        assertEquals(response.getPermission().get(0).getPermissionId(), permissionId);
        assertEquals(response.getPermission().get(0).getPermissionDescription(), permissionDescription);
        assertEquals(response.getDelegatablePermission().get(0).getRoleId(), roleId);
        assertEquals(response.getDelegatablePermission().get(0).getPermissionId(), permissionId);
    }

    @Test
    public void canPutMetadata() throws Exception {
        PutMetadataRequest request = new PutMetadataRequest();
        request.setDomain(domainId);
        request.setSystemId(systemId);
        request.setSystemLongName(systemDescription);

        DelegatingRole role = new DelegatingRole();
        role.setRoleId(roleId);
        role.setRoleDescription(roleDescription);
        request.getRole().add(role);

        SystemPermission permission = new SystemPermission();
        permission.setPermissionId(permissionId);
        permission.setPermissionDescription(permissionDescription);
        request.getPermission().add(permission);

        DelegatablePermission delegatablePermission = new DelegatablePermission();
        delegatablePermission.setRoleId(roleId);
        delegatablePermission.setPermissionId(permissionId);
        request.getDelegatablePermission().add(delegatablePermission);

        assertNotNull(service.putMetadata(request, soapHeader));

        verify(metadataManager).putMetadata(argThat(new TypeSafeMatcher<Metadata>() {
            @Override
            public boolean matchesSafely(Metadata item) {
                return allTrue(
                        item.getDomainId().equals(domainId),
                        item.getSystem().getDomainId().equals(systemId),
                        item.getSystem().getDescription().equals(systemDescription),
                        item.getRoles().get(0).getDomainId().equals(roleId),
                        item.getRoles().get(0).getDescription().equals(roleDescription),
                        item.getPermissions().get(0).getDomainId().equals(permissionId),
                        item.getPermissions().get(0).getDescription().equals(permissionDescription),
                        item.getDelegatablePermissions().get(0).getRoleId().equals(roleId),
                        item.getDelegatablePermissions().get(0).getPermissionId().equals(permissionId)
                );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void willAuthorizeWhitelistedSystemIdCard() {
        setupDgwsRequestContextForSystem("12345678");
        when(whitelistChecker.isSystemWhitelisted("getDelegations", "12345678")).thenReturn(true);
        service.authorizeOperationForCpr("getDelegations", "error message");
    }

    @Test(expected = IllegalAccessError.class)
    public void willDenyNonWhitelistedSystemIdCard() {
        setupDgwsRequestContextForSystem("12345678");
        when(whitelistChecker.isSystemWhitelisted("getDelegations", "12345678")).thenReturn(false);
        service.authorizeOperationForCpr("getDelegations", "error message");
    }

    @Test
    public void willDenyUserIdCardWhitelistedAsSystem() {
        setupDgwsRequestContextForUser("12345678");
        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, "12345678", null));
        when(whitelistChecker.isUserWhitelisted("getDelegations", "12345678", "1122334455")).thenReturn(false);
        try {
            service.authorizeOperationForCpr("getDelegations", "error message");
            fail();
        }
        catch (IllegalAccessError e) {
            // expected
        }
        verify(whitelistChecker, never()).isSystemWhitelisted(any(String.class), any(String.class));
    }

    @Test
    public void willAllowMinimalPutMetadata() throws Exception {
        PutMetadataRequest request = new PutMetadataRequest();
        request.setDomain(domainId);
        request.setSystemId(systemId);
        assertNotNull(service.putMetadata(request, soapHeader));

        verifyZeroInteractions(roleDao);
        verifyZeroInteractions(permissionDao);
        verifyZeroInteractions(delegationPermissionDao);
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
