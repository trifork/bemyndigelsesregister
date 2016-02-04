package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RettighedDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindRettighedBySystem() throws Exception {
        final List<Rettighed> rettigheder = rettighedDao.findBy(
                linkedSystemDao.get(1)
        );

        assertEquals(2, rettigheder.size());
        assertEquals("R01", rettigheder.get(0).getKode());
        assertEquals("R02", rettigheder.get(1).getKode());
    }
}
