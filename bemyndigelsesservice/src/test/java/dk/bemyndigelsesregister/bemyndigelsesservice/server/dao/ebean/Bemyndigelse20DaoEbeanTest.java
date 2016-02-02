package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.AssertTrue;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse20Rettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.Bemyndigelse20Dao;
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
public class Bemyndigelse20DaoEbeanTest extends DaoUnitTestSupport {
    @Inject
    Bemyndigelse20Dao dao;

    @Inject
    EbeanServer ebeanServer;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Test
    public void testOpretBemyndigelser() throws Exception {
        try {
            ebeanServer.beginTransaction();

            int n = dao.list().size();

            Bemyndigelse20 b = new Bemyndigelse20();
            b.setArbejdsfunktionKode(arbejdsfunktionDao.get(1).getKode());
            b.setBemyndigendeCpr("0101010AB1");
            b.setBemyndigedeCpr("0202020AB2");
            b.setBemyndigedeCvr("12345678");
            b.setGyldigFra(new DateTime(System.currentTimeMillis()));
            b.setGyldigTil(new DateTime(System.currentTimeMillis() + 20000000));
            b.setLinkedSystemKode(linkedSystemDao.get(1).getKode());
            b.setStatus(Status.BESTILT);
            b.setKode("Spas");

            Bemyndigelse20Rettighed rettighed = new Bemyndigelse20Rettighed();
            rettighed.setBemyndigelse(b);
            rettighed.setRettighedKode(rettighedDao.get(1).getKode());


            Set<Bemyndigelse20Rettighed> rettigheder = new HashSet<>();
            rettigheder.add(rettighed);
            b.setRettigheder(rettigheder);

            dao.save(b);

            assertEquals("Efter oprettelse forventes at antal bemyndigelser er steget med 1", n + 1, dao.list().size());
        } finally {
            ebeanServer.rollbackTransaction();
        }
    }

    @Test
    public void testRetBemyndigelser() throws Exception {
        try {
            ebeanServer.beginTransaction();

            Bemyndigelse20 bemyndigelse = dao.get(1);
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
        final List<Bemyndigelse20> bemyndigelser = dao.list();

        assertEquals("Antal bemyndigelser afviger fra det forventede", 3, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByBemyndigende() throws Exception {
        final List<Bemyndigelse20> bemyndigelser = dao.findByBemyndigendeCpr("1010101010");

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByBemyndigede() throws Exception {
        final List<Bemyndigelse20> bemyndigelser = dao.findByBemyndigedeCpr("1010101012");

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }

    @Test
    public void testFindByKoder() throws Exception {
        final List<Bemyndigelse20> bemyndigelser = dao.findByKoder(Arrays.asList("TestKode1", "TestKode3"));

        assertEquals("Antal bemyndigelser afviger fra det forventede", 2, bemyndigelser.size());

        System.out.println(bemyndigelser);
    }
}
