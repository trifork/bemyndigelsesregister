package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Arbejdsfunktion;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArbejdsfunktionDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindArbejdsfunktionSystem() throws Exception {
        final List<Arbejdsfunktion> arbejdsfunktioner = arbejdsfunktionDao.findBy(
                linkedSystemDao.get(1)
        );
        assertEquals(1, arbejdsfunktioner.size());
        assertEquals("Laege", arbejdsfunktioner.get(0).getKode());
    }
}
