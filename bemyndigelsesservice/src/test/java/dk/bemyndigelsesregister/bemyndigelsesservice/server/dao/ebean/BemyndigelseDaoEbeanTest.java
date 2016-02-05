package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.BemyndigelseDao;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BemyndigelseDaoEbeanTest extends DaoUnitTestSupport {
    @Inject
    BemyndigelseDao dao;

    @Value("${jdbc.url}")
    String jdbcUrl;

    @Test
    public void willFindOnlyBemyndigelserModifiedSince() throws Exception {
        final DateTime startTime = new DateTime(2011, 5, 22, 0, 0);
        final List<Bemyndigelse> bemyndigelser = dao.findBySidstModificeretGreaterThanOrEquals(startTime);
        assertEquals(1, bemyndigelser.size());
        assertEquals("TestKode2", bemyndigelser.get(0).getKode());
    }

    @Test
    public void willFindBemyndigelseWithKode() throws Exception {
        final String kode = "TestKode1";

        Bemyndigelse bemyndigelse = dao.findByKode(kode);
        assertEquals(new Long(1l), bemyndigelse.getUUID());
        assertEquals(kode, bemyndigelse.getKode());
    }

    @Test
    public void willFindBemyndigelseWhenWhenPeriodWraps() throws Exception {
        Bemyndigelse other = dao.findByKode("TestKode3");

        final List<Bemyndigelse> found = dao.findByInPeriod(
                "1010101013",
                "1",
                arbejdsfunktionDao.get(1).getKode(),
                rettighedDao.get(1).getKode(),
                linkedSystemDao.get(1).getKode(),
                other.getGyldigFra().minusYears(1),
                other.getGyldigTil().plusYears(1)
        );

        assertEquals(1, found.size());
        assertEquals("TestKode3", found.get(0).getKode());
    }

    @Test
    public void willFindBemyndigelseWhenWhenPeriodWrapsGyldigFra() throws Exception {
        Bemyndigelse other = dao.findByKode("TestKode3");

        final List<Bemyndigelse> found = dao.findByInPeriod(
                "1010101013",
                "1",
                arbejdsfunktionDao.get(1).getKode(),
                rettighedDao.get(1).getKode(),
                linkedSystemDao.get(1).getKode(),
                other.getGyldigFra().minusYears(1),
                other.getGyldigFra().plusHours(1)
        );

        assertEquals(1, found.size());
        assertEquals("TestKode3", found.get(0).getKode());
    }

    @Test
    public void willFindBemyndigelseWhenWhenPeriodWrapsGyldigTil() throws Exception {
        Bemyndigelse other = dao.findByKode("TestKode3");

        final List<Bemyndigelse> found = dao.findByInPeriod(
                "1010101013",
                "1",
                arbejdsfunktionDao.get(1).getKode(),
                rettighedDao.get(1).getKode(),
                linkedSystemDao.get(1).getKode(),
                other.getGyldigTil().minusHours(1),
                other.getGyldigTil().plusYears(1)
        );

        assertEquals(1, found.size());
        assertEquals("TestKode3", found.get(0).getKode());
    }

    @Test
    public void willNotFindWhenPeriodIsBefore() throws Exception {
        Bemyndigelse other = dao.findByKode("TestKode3");

        final List<Bemyndigelse> found = dao.findByInPeriod(
                "1010101013",
                "1",
                arbejdsfunktionDao.get(1).getKode(),
                rettighedDao.get(1).getKode(),
                linkedSystemDao.get(1).getKode(),
                other.getGyldigFra().minusYears(1),
                other.getGyldigFra().minusHours(1)
        );

        assertEquals(0, found.size());
    }

    @Test
    public void willNotFindWhenPeriodIsAfter() throws Exception {
        Bemyndigelse other = dao.findByKode("TestKode3");

        final List<Bemyndigelse> found = dao.findByInPeriod(
                "1010101013",
                "1",
                arbejdsfunktionDao.get(1).getKode(),
                rettighedDao.get(1).getKode(),
                linkedSystemDao.get(1).getKode(),
                other.getGyldigTil().plusHours(1),
                other.getGyldigTil().plusYears(1)
        );

        assertEquals(0, found.size());
    }
}
