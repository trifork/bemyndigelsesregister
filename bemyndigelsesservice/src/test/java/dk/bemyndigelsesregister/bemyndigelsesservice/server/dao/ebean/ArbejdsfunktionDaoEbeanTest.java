package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArbejdsfunktionDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindArbejdsFunktionByDomaene() throws Exception {
        final List<Arbejdsfunktion> foundByDomaene = arbejdsfunktionDao.findBy(domaeneDao.get(1l));
        assertEquals(1, foundByDomaene.size());
        assertEquals("For unit test only", foundByDomaene.get(0).getBeskrivelse());
    }

    @Test
    public void canFindArbejdsfunktionByDomaeneAndSystem() throws Exception {
        final List<Arbejdsfunktion> arbejdsfunktioner = arbejdsfunktionDao.findBy(
                domaeneDao.get(1),
                linkedSystemDao.get(1)
        );
        assertEquals(1, arbejdsfunktioner.size());
        assertEquals("Laege", arbejdsfunktioner.get(0).getKode());
    }
}
