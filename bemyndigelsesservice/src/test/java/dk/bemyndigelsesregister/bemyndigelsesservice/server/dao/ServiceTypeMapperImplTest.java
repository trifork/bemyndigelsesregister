package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Permission;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTypeMapperImplTest {
    @InjectMocks
    private ServiceTypeMapperImpl typeMapper = new ServiceTypeMapperImpl();

    @Mock
    RoleDao roleDao;

    @Mock
    DelegatingSystemDao delegatingSystemDao;

    @Mock
    PermissionDao permissionDao;

    private String domainId = "UUID kode";
    private String delegatorCpr = "BemyndigendeCpr";
    private String delegateeCpr = "BemyndigedeCpr";
    private String delegateeCvr = "BemyndigedeCvr";
    private String roleId = "Arbejdsfunktionskode";
    private String systemId = "SystemKode";
    private String permissionId1 = "P1";
    private String permissionId2 = "P2";
    private List<String> permissionIds = Arrays.asList(permissionId1, permissionId2);
    private State state = State.GODKENDT;
    private DateTime now = DateTime.now();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void willMapToDelegation() throws Exception {
        Permission p1 = new Permission();
        p1.setDomainId(permissionId1);
        p1.setDescription("First Permission");
        when(permissionDao.findByDomainId(systemId, permissionId1)).thenReturn(p1);

        Permission p2 = new Permission();
        p2.setDomainId(permissionId2);
        p2.setDescription("Second Permission");
        when(permissionDao.findByDomainId(systemId, permissionId2)).thenReturn(p2);

        dk.nsi.bemyndigelse._2016._01._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals(domainId, d.getDelegationId());
        assertEquals(delegatorCpr, d.getDelegatorCpr());
        assertEquals(delegateeCpr, d.getDelegateeCpr());
        assertEquals(delegateeCvr, d.getDelegateeCvr());
        assertEquals(dk.nsi.bemyndigelse._2016._01._01.State.GODKENDT, d.getState());
        assertEquals(2, d.getPermission().size());
        assertNotNull(d.getCreated());
        assertNotNull(d.getEffectiveFrom());
        assertNotNull(d.getEffectiveTo());
    }

    @Test
    public void willNotMapPermissionsWithoutMetadata() throws Exception {
        Permission p1 = new Permission();
        p1.setDomainId(permissionId1);
        p1.setDescription("First Permission");
        when(permissionDao.findByDomainId(systemId, permissionId1)).thenReturn(p1);

        dk.nsi.bemyndigelse._2016._01._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals("Mappet bemyndigelse skal kun indeholde én rettighed, da kun én er defineret", 1, d.getPermission().size());
        assertEquals("Mappet bemyndigelse skal indeholde den definerede rettighed", permissionId1, d.getPermission().get(0).getPermissionId());
    }

    private Delegation createDelegation() {
        Delegation delegation = new Delegation();

        delegation.setDomainId(domainId);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);
        delegation.setDelegatingSystem(systemId);
        delegation.setRole(roleId);
        delegation.setState(state);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionIds) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(delegation);
            permission.setPermissionId(permissionId);
            permissionList.add(permission);
        }
        delegation.setDelegationPermissions(permissionList);

        delegation.setCreated(now);
        delegation.setEffectiveFrom(now.minusDays(1));
        delegation.setEffectiveTo(now.plusDays(1));

        return delegation;
    }
}
