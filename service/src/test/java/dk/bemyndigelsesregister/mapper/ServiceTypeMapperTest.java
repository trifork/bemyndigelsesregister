package dk.bemyndigelsesregister.mapper;

import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.DelegationPermission;
import dk.bemyndigelsesregister.domain.Status;
import dk.bemyndigelsesregister.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertNotNull;

@SpringBootTest
public class ServiceTypeMapperTest {
    private String code = "UUID code";
    private String delegatorCpr = "delegatorCpr";
    private String delegateeCpr = "delegateeCpr";
    private String delegateeCvr = "delegateeCvr";
    private String roleCode = "roleCode";
    private List<String> permissionCodes = Arrays.asList(TestData.permissionCode1, TestData.permissionCode2);
    private Status state = Status.GODKENDT;
    private Instant now = Instant.now();

    @Autowired
    private ServiceTypeMapper typeMapper;

    @Test
    public void willMapToDelegation() {
        dk.nsi.bemyndigelse._2017._08._01.Delegation d = typeMapper.toDelegationType(createDelegation());

        assertNotNull(d);
        assertEquals(code, d.getDelegationId());
        assertEquals(delegatorCpr, d.getDelegatorCpr());
        assertEquals(delegateeCpr, d.getDelegateeCpr());
        assertEquals(delegateeCvr, d.getDelegateeCvr());
        assertEquals(dk.nsi.bemyndigelse._2017._08._01.State.GODKENDT, d.getState());
        assertEquals(2, d.getPermissions().size());
        assertNotNull(d.getCreated());
        assertNotNull(d.getEffectiveFrom());
        assertNotNull(d.getEffectiveTo());
    }

    private Delegation createDelegation() {
        Delegation delegation = new Delegation();

        delegation.setCode(code);
        delegation.setDelegatorCpr(delegatorCpr);
        delegation.setDelegateeCpr(delegateeCpr);
        delegation.setDelegateeCvr(delegateeCvr);
        delegation.setSystemCode(TestData.systemCode);
        delegation.setRoleCode(roleCode);
        delegation.setState(state);

        Set<DelegationPermission> permissionList = new HashSet<>();
        for (String permissionId : permissionCodes) {
            final DelegationPermission permission = new DelegationPermission();
            permission.setDelegationId(delegation.getId());
            permission.setPermissionCode(permissionId);
            permissionList.add(permission);
        }
        delegation.setDelegationPermissions(permissionList);

        delegation.setCreated(now);
        delegation.setEffectiveFrom(DateUtils.plusDays(now, -1));
        delegation.setEffectiveTo(DateUtils.plusDays(now, 1));

        return delegation;
    }
}
