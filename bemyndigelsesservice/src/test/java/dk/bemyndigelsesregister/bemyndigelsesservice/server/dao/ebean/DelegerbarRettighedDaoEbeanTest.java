package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DelegerbarRettighedDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindDelegerbarRettighedByDomaeneAndSystem() throws Exception {
        final List<DelegerbarRettighed> delegerbarRettigheder = delegerbarRettighedDao.findBy(
                domaeneDao.get(1),
                linkedSystemDao.get(1)
        );
        assertEquals(1, delegerbarRettigheder.size());
        assertEquals("DR01", delegerbarRettigheder.get(0).getKode());
    }
}
