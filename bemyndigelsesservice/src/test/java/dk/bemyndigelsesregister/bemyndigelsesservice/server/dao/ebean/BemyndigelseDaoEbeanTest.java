package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.*;

public class BemyndigelseDaoEbeanTest extends DaoUnitTestSupport {
    @Inject
    BemyndigelseDao dao;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Test
    public void willFindOnlyBemyndigelserModifiedSince() throws Exception {
        final DateTime startTime = new DateTime(2011, 5, 22, 0, 0);
        final List<Bemyndigelse> bemyndigelser = dao.findBySidstModificeretGreaterThan(startTime);
        assertEquals(1, bemyndigelser.size());
        assertEquals("TestKode2", bemyndigelser.get(0).getKode());
    }

    @Test
    public void willFindBemyndigelseWithKode() throws Exception {
        final String kode = "TestKode1";

        Bemyndigelse bemyndigelse = dao.findByKode(kode);
        assertEquals(new Long(1l), bemyndigelse.getId());
        assertEquals(kode, bemyndigelse.getKode());
    }
}
