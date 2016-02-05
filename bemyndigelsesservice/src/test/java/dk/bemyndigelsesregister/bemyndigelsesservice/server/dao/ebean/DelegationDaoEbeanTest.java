package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.*;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DelegationDao;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.*;

import static org.junit.Assert.assertEquals;
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
    public void testOpretBemyndigelser() throws Exception {
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

            assertEquals("Efter oprettelse forventes at antal bemyndigelser er steget med 1", n + 1, dao.list().size());
        } finally {
            ebeanServer.rollbackTransaction();
        }
    }

    @Test
    public void testRetBemyndigelser() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Delegation bemyndigelse = dao.get(1);
            bemyndigelse.setSidstModificeretAf("TestCase was here: " + System.currentTimeMillis());
            dao.save(bemyndigelse);

            bemyndigelse = dao.get(1);
            assertTrue("Tekst \"" + bemyndigelse.getSidstModificeretAf() + "\" skal starte med \"TestCase was here\"", bemyndigelse.getSidstModificeretAf().startsWith("TestCase was here"));
        } finally {
            ebeanServer.rollbackTransaction();
        }
    }

    @Test
    public void testListBemyndigelser() throws Exception {
        final List<Delegation> bemyndigelser = dao.list();

        assertEquals("Antal bemyndigelser afviger fra det forventede", 3, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByBemyndigende() throws Exception {
        final List<Delegation> bemyndigelser = dao.findByDelegatorCpr("1010101010");

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByBemyndigede() throws Exception {
        final List<Delegation> bemyndigelser = dao.findByDelegateeCpr("1010101012");

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByKoder() throws Exception {
        final List<Delegation> bemyndigelser = dao.findByDomainIds(Arrays.asList("TestKode1", "TestKode3"));

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }
}
