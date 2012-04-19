package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ArbejdsfunktionDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.DomaeneDao;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.LinkedSystemDao;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArbejdsfunktionDaoEbeanTest extends DaoUnitTestSupport {

    @Inject
    ArbejdsfunktionDao dao;

    @Inject
    DomaeneDao domaeneDao;

    @Inject
    LinkedSystemDao linkedSystemDao;

    @Test
    public void canFindArbejdsFunktionByDomaene() throws Exception {
        final List<Arbejdsfunktion> foundByDomaene = dao.findBy(domaeneDao.get(1l));
        assertEquals(1, foundByDomaene.size());
        assertEquals("For unit test only", foundByDomaene.get(0).getBeskrivelse());
    }
}
