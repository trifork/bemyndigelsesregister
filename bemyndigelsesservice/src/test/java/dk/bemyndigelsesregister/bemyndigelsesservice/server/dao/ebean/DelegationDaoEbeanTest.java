package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegationPermission;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.State;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * BEM 2.0 rettighed til bemyndigelse
 * Created by obj on 02-02-2016.
 */
public class DelegationDaoEbeanTest extends DaoUnitTestSupport {
    @Inject
    DelegationDao dao;

    @Inject
    EbeanServer ebeanServer;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Test
    public void testCreateDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            int n = dao.list().size();

            Delegation d = new Delegation();
            d.setRole(roleDao.get(1));
            d.setDelegatorCpr("0101010AB1");
            d.setDelegateeCpr("0202020AB2");
            d.setDelegateeCvr("12345678");
            d.setEffectiveFrom(new DateTime(System.currentTimeMillis()));
            d.setEffectiveTo(new DateTime(System.currentTimeMillis() + 20000000));
            d.setDelegatingSystem(systemDao.get(1));
            d.setState(State.GODKENDT);
            d.setDelegatingSystem(new DelegatingSystem() {{
                setDomainId("Spas");
            }});

            DelegationPermission permission = new DelegationPermission();
            permission.setDelegation(d);
            permission.setPermissionId(delegationPermissionDao.get(1).getPermissionId());

            Set<DelegationPermission> permissions = d.getDelegationPermissions();
            permissions.add(permission);

            dao.save(d);

            assertEquals("After save the no. of delegations should increase by one", n + 1, dao.list().size());
        } finally {
            ebeanServer.endTransaction();
            ;
        }
    }

    @Test
    public void testUpdateDelegation() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation delegation = dao.get(1);
            delegation.setSidstModificeretAf("TestCase was here: " + System.currentTimeMillis());
            dao.save(delegation);

            delegation = dao.get(1);
            assertTrue("Text \"" + delegation.getSidstModificeretAf() + "\" should start with \"TestCase was here\"", delegation.getSidstModificeretAf().startsWith("TestCase was here"));
        } finally {
            ebeanServer.endTransaction();
        }
    }

    @Test
    public void testGetDelegation() throws Exception {
        Delegation delegation = dao.get(1);
        System.out.println(delegation);
        assertNotNull(delegation);

    }

    @Test
    public void testListDelegations() throws Exception {
        final List<Delegation> delegations = dao.list();

        assertEquals("Unexpected no. of delegations found", 3, delegations.size());

        System.out.println(delegations);
    }

    @Test
    public void testFindByDelegator() throws Exception {
        final List<Delegation> delegations = dao.findByDelegatorCpr("1010101010");

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());

        System.out.println(delegations);
    }

    @Test
    public void testFindByDelegatee() throws Exception {
        final List<Delegation> delegations = dao.findByDelegateeCpr("1010101012");

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());

        System.out.println(delegations);
    }

    @Test
    public void testFindByDomainIds() throws Exception {
        final List<Delegation> delegations = dao.findByDomainIds(Arrays.asList("TestKode1", "TestKode3"));

        assertEquals("Unexpected no. of delegations found", 2, delegations.size());

        System.out.println(delegations);
    }
}
