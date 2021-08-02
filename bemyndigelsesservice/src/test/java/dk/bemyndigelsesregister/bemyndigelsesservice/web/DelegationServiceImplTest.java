package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.DelegationManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.audit.AuditLogger;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DelegationServiceImplTest {
    @InjectMocks
    BemyndigelsesServiceImpl_20160101 service = new BemyndigelsesServiceImpl_20160101();

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
    ServiceTypeMapper_20160101 typeMapper;
    @Mock
    MetadataManager metadataManager;
    @Mock
    AuditLogger auditLogger;

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

    final String code = "UUID code";
    final String delegatorCprText = "DelegatorCpr";
    final String delegateeCprText = "DelegateeCpr";
    final String delegateeCvrText = "DelegateeCvr";
    final String roleCode = "RoleCode";
    final String roleDescription = "RoleDescription";
    final String systemCode = "SystemCode";
    final String systemDescription = "SystemDescription";
    final String permissionCode = "PermissionCode";
    final String permissionDescription = "PermissionDescription";
    final String permissionCode1 = "P1";
    final String permissionCode2 = "P2";
    final List<String> permissionCodes = Arrays.asList(permissionCode1, permissionCode2);
    final Status state = Status.GODKENDT;
//
//    void setupDgwsRequestContextForSystem(String cvr) {
//        when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.SYSTEM, 3));
//        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, cvr, null));
//    }
//
//    void setupDgwsRequestContextForUser(String cpr) {
//        when(dgwsRequestContext.getIdCardData()).thenReturn(new IdCardData(IdCardType.USER, 4));
//        when(dgwsRequestContext.getIdCardUserLog()).thenReturn(new IdCardUserLog(cpr, null, null, null, null, null, null));
//    }

    @Test
    public void canCreateDelegationAsDelegator() throws Exception {
        final Delegation delegation = createDelegation(code, state, null);

        when(delegationManager.createDelegation(systemCode, delegatorCprText, delegateeCprText, delegateeCvrText, roleCode, state, permissionCodes, null, null)).thenReturn(delegation);
//        setupDgwsRequestContextForUser(delegatorCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemCode);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleCode);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionCodes);
                setListOfPermissionIds(pIds);
                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
            }});
        }};

        final CreateDelegationsResponse response = service.createDelegations(request, soapHeader);

        verify(delegationManager).createDelegation(systemCode, delegatorCprText, delegateeCprText, delegateeCvrText, roleCode, state, permissionCodes, null, null);

        assertEquals(1, response.getDelegation().size());
        verify(typeMapper).toDelegationType(delegation);
    }

    @Test
    public void canCreateDelegationAsDelegatee() throws Exception {
        final Delegation delegation = createDelegation(code, Status.ANMODET, null);

        when(delegationManager.createDelegation(systemCode, delegatorCprText, delegateeCprText, delegateeCvrText, roleCode, Status.ANMODET, permissionCodes, null, null)).thenReturn(delegation);
//        setupDgwsRequestContextForUser(delegateeCprText);

        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
            getCreate().add(new Create() {{
                setSystemId(DelegationServiceImplTest.this.systemCode);
                setDelegatorCpr(delegatorCprText);
                setDelegateeCpr(delegateeCprText);
                setDelegateeCvr(delegateeCvrText);
                setRoleId(DelegationServiceImplTest.this.roleCode);

                ListOfPermissionIds pIds = new ListOfPermissionIds();
                pIds.getPermissionId().addAll(permissionCodes);
                setListOfPermissionIds(pIds);
                setState(State.ANMODET);
            }});
        }};

        final CreateDelegationsResponse response = service.createDelegations(request, soapHeader);

        verify(delegationManager).createDelegation(systemCode, delegatorCprText, delegateeCprText, delegateeCvrText, roleCode, Status.ANMODET, permissionCodes, null, null);

        assertEquals(1, response.getDelegation().size());
        verify(typeMapper).toDelegationType(delegation);
    }

    //TODO: Needs to be an integrationtest
//    @Test(expected = IllegalAccessError.class)
//    public void cannotCreateDelegationForThirdPartyAsDelegator() throws Exception {
//        final Delegation delegation = createDelegation(code, state, null);
//
////        setupDgwsRequestContextForUser(delegatorCprText);
//
//        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
//            getCreate().add(new Create() {{
//                setSystemId(DelegationServiceImplTest.this.systemCode);
//                setDelegatorCpr("anotherCpr");
//                setDelegateeCpr(delegateeCprText);
//                setDelegateeCvr(delegateeCvrText);
//                setRoleId(DelegationServiceImplTest.this.roleCode);
//
//                ListOfPermissionIds pIds = new ListOfPermissionIds();
//                pIds.getPermissionId().addAll(permissionCodes);
//                setListOfPermissionIds(pIds);
//                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
//            }});
//        }};
//
//        service.createDelegations(request, soapHeader);
//    }

    // TODO: Needs to be an integrationtest
//    @Test(expected = IllegalAccessError.class)
//    public void cannotCreateDelegationWithStateGodkendtAsDelegatee() throws Exception {
//        final Delegation delegation = createDelegation(code, state, null);
//
////        setupDgwsRequestContextForUser(delegateeCprText);
//
//        CreateDelegationsRequest request = new CreateDelegationsRequest() {{
//            getCreate().add(new Create() {{
//                setSystemId(DelegationServiceImplTest.this.systemCode);
//                setDelegatorCpr(delegatorCprText);
//                setDelegateeCpr(delegateeCprText);
//                setDelegateeCvr(delegateeCvrText);
//                setRoleId(DelegationServiceImplTest.this.roleCode);
//
//                ListOfPermissionIds pIds = new ListOfPermissionIds();
//                pIds.getPermissionId().addAll(permissionCodes);
//                setListOfPermissionIds(pIds);
//                setState(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT);
//            }});
//        }};
//
//        service.createDelegations(request, soapHeader);
//    }

    private Delegation createDelegation(final String id, Status state, DateTime creationDate) {
        final Delegation delegation = new Delegation();

        delegation.setCode(id);

        delegation.setDelegatorCpr(delegatorCprText);
        delegation.setDelegateeCpr(delegateeCprText);
        delegation.setDelegateeCvr(delegateeCvrText);
        delegation.setSystemCode(this.systemCode);
        delegation.setRoleCode(this.roleCode);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionCodes) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(delegation);
            permission.setPermissionCode(permissionId);
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
        final Delegation delegation = createDelegation(code, state, null);

        when(delegationManager.getDelegationsByDelegatorCpr(delegatorCprText)).thenReturn(Arrays.asList(delegation));
//        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegationsByDelegatorCpr(delegatorCprText);

        assertEquals(1, response.getDelegation().size());
    }

    @Test
    public void canGetDelegationsByDelegateeCpr() throws Exception {
        final Delegation delegation = createDelegation(code, state, null);

        when(delegationManager.getDelegationsByDelegateeCpr(delegateeCprText)).thenReturn(Arrays.asList(delegation));
//        setupDgwsRequestContextForUser(delegateeCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegateeCpr(delegateeCprText);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegationsByDelegateeCpr(delegateeCprText);

        assertEquals(1, response.getDelegation().size());
    }

    @Test
    public void canGetDelegationsById() throws Exception {
        final Delegation delegation = createDelegation(code, state, null);

        when(delegationManager.getDelegation(code)).thenReturn(delegation);
//        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegationId(code);
        }};
        final GetDelegationsResponse response = service.getDelegations(request, soapHeader);

        verify(delegationManager).getDelegation(code);

        assertEquals(1, response.getDelegation().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetDelegationsByBadArguments() throws Exception {
//        setupDgwsRequestContextForUser(delegatorCprText);

        GetDelegationsRequest request = new GetDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            setDelegateeCpr(delegateeCprText);
        }};
        service.getDelegations(request, soapHeader);
    }


    @Test
    public void canDeleteDelegationsAsDelegator() throws Exception {
        when(delegationManager.deleteDelegation(delegatorCprText, null, code, now.plusDays(1))).thenReturn(code);
//        setupDgwsRequestContextForUser(delegatorCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(code);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(delegatorCprText, null, code, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test
    public void canDeleteDelegationsAsDelegatee() throws Exception {
        when(delegationManager.deleteDelegation(null, delegateeCprText, code, now.plusDays(1))).thenReturn(code);
//        setupDgwsRequestContextForUser(delegateeCprText);

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(code);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        final DeleteDelegationsResponse response = service.deleteDelegations(request, soapHeader);

        verify(delegationManager).deleteDelegation(null, delegateeCprText, code, now.plusDays(1));

        assertEquals(1, response.getDelegationId().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canDeleteDelegationsByDelegatorAndDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            setDelegatorCpr(delegatorCprText);
            setDelegateeCpr(delegateeCprText);
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(code);
            setListOfDelegationIds(listOfDelegationIds);
            setDeletionDate(toXmlGregorianCalendar(now.plusDays(1)));
        }};
        service.deleteDelegations(request, soapHeader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetDelegationsByNoDelegatorNorDelegatee() throws Exception {

        DeleteDelegationsRequest request = new DeleteDelegationsRequest() {{
            ListOfDelegationIds listOfDelegationIds = new ListOfDelegationIds();
            listOfDelegationIds.getDelegationId().add(code);
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
        request.setDomain(code);
        request.setSystemId(systemCode);

        Metadata metadata = new Metadata(code, systemCode, systemDescription);
        metadata.addRole(roleCode, roleDescription);
        metadata.addPermission(permissionCode, permissionDescription);
        metadata.addDelegatablePermission(roleCode, permissionCode, permissionDescription, true);

        when(metadataManager.getMetadata(code, systemCode)).thenReturn(metadata);

        GetMetadataResponse response = service.getMetadata(request, soapHeader);

        assertEquals(code, response.getDomain());
        assertEquals(systemCode, response.getSystem().getSystemId());
        assertEquals(systemDescription, response.getSystem().getSystemLongName());
        assertEquals(1, response.getRole().size());
        assertEquals(roleCode, response.getRole().get(0).getRoleId());
        assertEquals(roleDescription, response.getRole().get(0).getRoleDescription());
        assertEquals(permissionCode, response.getRole().get(0).getDelegatablePermissions().getPermissionId().get(0));
        assertEquals(1, response.getPermission().size());
        assertEquals(permissionCode, response.getPermission().get(0).getPermissionId());
        assertEquals(permissionDescription, response.getPermission().get(0).getPermissionDescription());
        assertFalse(response.isEnableAsteriskPermission());
    }

    @Test
    public void canGetMetadataWithAsteriskPermission() throws Exception {
        GetMetadataRequest request = new GetMetadataRequest();
        request.setDomain(code);
        request.setSystemId(systemCode);

        Metadata metadata = new Metadata(code, systemCode, systemDescription);
        metadata.addRole(roleCode, roleDescription);
        metadata.addPermission(permissionCode, permissionDescription);
        metadata.addPermission(Metadata.ASTERISK_PERMISSION_CODE, Metadata.ASTERISK_PERMISSION_DESCRIPTION);
        metadata.addDelegatablePermission(roleCode, permissionCode, permissionDescription, true);

        when(metadataManager.getMetadata(code, systemCode)).thenReturn(metadata);

        GetMetadataResponse response = service.getMetadata(request, soapHeader);

        assertTrue(response.isEnableAsteriskPermission());
        assertEquals(2, response.getPermission().size());
        for (SystemPermission p : response.getPermission())
            assertTrue(Arrays.asList(permissionCode, Metadata.ASTERISK_PERMISSION_CODE).contains(p.getPermissionId()));
    }

    @Test
    public void canPutMetadata() throws Exception {
        PutMetadataRequest request = new PutMetadataRequest();
        request.setDomain(code);
        request.setSystemId(systemCode);
        request.setSystemLongName(systemDescription);

        SystemPermission permission = new SystemPermission();
        permission.setPermissionId(permissionCode);
        permission.setPermissionDescription(permissionDescription);
        request.getPermission().add(permission);

        DelegatingRole role = new DelegatingRole();
        role.setRoleId(roleCode);
        role.setRoleDescription(roleDescription);

        DelegatingRole.DelegatablePermissions p = new DelegatingRole.DelegatablePermissions();
        p.getPermissionId().add(permissionCode);
        role.setDelegatablePermissions(p);

        request.getRole().add(role);

        assertNotNull(service.putMetadata(request, soapHeader));

        verify(metadataManager).putMetadata(argThat(new TypeSafeMatcher<Metadata>() {
            @Override
            public boolean matchesSafely(Metadata item) {
                return allTrue(
                        item.getDomainCode().equals(code),
                        item.getSystem().getCode().equals(systemCode),
                        item.getSystem().getDescription().equals(systemDescription),
                        item.getRoles().get(0).getCode().equals(roleCode),
                        item.getRoles().get(0).getDescription().equals(roleDescription),
                        item.getPermissions().get(0).getCode().equals(permissionCode),
                        item.getPermissions().get(0).getDescription().equals(permissionDescription),
                        item.getDelegatablePermissions().get(0).getRoleCode().equals(roleCode),
                        item.getDelegatablePermissions().get(0).getPermissionCode().equals(permissionCode)
                );
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }
//
//    @Test
//    public void willAuthorizeWhitelistedSystemIdCard() {
//        setupDgwsRequestContextForSystem("12345678");
//        when(whitelistChecker.isSystemWhitelisted("getDelegations", "12345678")).thenReturn(true);
//        service.authorizeOperationForCpr("getDelegations", "error message");
//    }
//
//    @Test(expected = IllegalAccessError.class)
//    public void willDenyNonWhitelistedSystemIdCard() {
//        setupDgwsRequestContextForSystem("12345678");
//        when(whitelistChecker.isSystemWhitelisted("getDelegations", "12345678")).thenReturn(false);
//        service.authorizeOperationForCpr("getDelegations", "error message");
//    }
//
//    @Test
//    public void willDenyUserIdCardWhitelistedAsSystem() {
//        setupDgwsRequestContextForUser("12345678");
//        when(dgwsRequestContext.getIdCardSystemLog()).thenReturn(new IdCardSystemLog(null, CareProviderIdType.CVR_NUMBER, "12345678", null));
//        when(whitelistChecker.isUserWhitelisted("getDelegations", "12345678", "1122334455")).thenReturn(false);
//        try {
//            service.authorizeOperationForCpr("getDelegations", "error message");
//            fail();
//        } catch (IllegalAccessError e) {
//            // expected
//        }
//        verify(whitelistChecker, never()).isSystemWhitelisted(any(String.class), any(String.class));
//    }

    @Test
    public void willAllowMinimalPutMetadata() throws Exception {
        PutMetadataRequest request = new PutMetadataRequest();
        request.setDomain(code);
        request.setSystemId(systemCode);
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
