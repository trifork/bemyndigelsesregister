package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Rettighed;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RettighedDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindRettighedByDomaeneAndSystem() throws Exception {
        final List<Rettighed> rettigheder = rettighedDao.findBy(
                domaeneDao.get(1),
                linkedSystemDao.get(1)
        );

        assertEquals(1, rettigheder.size());
        assertEquals("R01", rettigheder.get(0).getKode());
    }
}
