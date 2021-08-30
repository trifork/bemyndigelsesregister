package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.MetadataManager;
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
    private ServiceTypeMapperImpl_20170801 typeMapper = new ServiceTypeMapperImpl_20170801();

    @Mock
    RoleDao roleDao;

    @Mock
    DelegatingSystemDao delegatingSystemDao;

    @Mock
    PermissionDao permissionDao;

    @Mock
    MetadataManager metadataManager;

    private String code = "UUID code";
    private String delegatorCpr = "delegatorCpr";
    private String delegateeCpr = "delegateeCpr";
    private String delegateeCvr = "delegateeCvr";
    private String roleCode = "roleCode";
    private String systemCode = "SystemCode";
    private String systemDescription = "SystemDescription";
    private String permissionCode1 = "P1";
    private String permissionCode2 = "P2";
    private List<String> permissionCodes = Arrays.asList(permissionCode1, permissionCode2);
    private Status state = Status.GODKENDT;
    private DateTime now = DateTime.now();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void willMapToDelegation() {
        Permission p1 = new Permission();
        p1.setCode(permissionCode1);
        p1.setDescription("First Permission");
        when(permissionDao.findByCode(systemCode, permissionCode1)).thenReturn(p1);
        when(metadataManager.getMetadata(null, systemCode)).thenReturn(new Metadata(null, systemCode, systemDescription));

        Permission p2 = new Permission();
        p2.setCode(permissionCode2);
        p2.setDescription("Second Permission");
        when(permissionDao.findByCode(systemCode, permissionCode2)).thenReturn(p2);

        dk.nsi.bemyndigelse._2017._08._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals(code, d.getDelegationId());
        assertEquals(delegatorCpr, d.getDelegatorCpr());
        assertEquals(delegateeCpr, d.getDelegateeCpr());
        assertEquals(delegateeCvr, d.getDelegateeCvr());
        assertEquals(dk.nsi.bemyndigelse._2017._08._01.State.GODKENDT, d.getState());
        assertEquals(2, d.getPermission().size());
        assertNotNull(d.getCreated());
        assertNotNull(d.getEffectiveFrom());
        assertNotNull(d.getEffectiveTo());
    }

    @Test
    public void willNotMapPermissionsWithoutMetadata() {
        Permission p1 = new Permission();
        p1.setCode(permissionCode1);
        p1.setDescription("First Permission");
        when(permissionDao.findByCode(systemCode, permissionCode1)).thenReturn(p1);
        when(metadataManager.getMetadata(null, systemCode)).thenReturn(new Metadata(null, systemCode, systemDescription));

        dk.nsi.bemyndigelse._2017._08._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals("Mappet bemyndigelse skal kun indeholde én rettighed, da kun én er defineret", 1, d.getPermission().size());
        assertEquals("Mappet bemyndigelse skal indeholde den definerede rettighed", permissionCode1, d.getPermission().get(0).getPermissionId());
    }

    private Delegation createDelegation() {
        Delegation delegation = new Delegation();

        delegation.setCode(code);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);
        delegation.setSystemCode(systemCode);
        delegation.setRoleCode(roleCode);
        delegation.setState(state);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionCodes) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(delegation);
            permission.setPermissionCode(permissionId);
            permissionList.add(permission);
        }
        delegation.setDelegationPermissions(permissionList);

        delegation.setCreated(now);
        delegation.setEffectiveFrom(now.minusDays(1));
        delegation.setEffectiveTo(now.plusDays(1));

        return delegation;
    }
}
