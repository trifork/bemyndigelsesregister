package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegerbarRettighed;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DelegerbarRettighedDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void canFindDelegerbarRettighedBySystem() throws Exception {
        final List<DelegerbarRettighed> delegerbarRettigheder = delegerbarRettighedDao.findBy(
                linkedSystemDao.get(1)
        );
        assertEquals(1, delegerbarRettigheder.size());
        assertEquals(TestData.roleCode, delegerbarRettigheder.get(0).getArbejdsfunktion().getKode());
        assertEquals(TestData.permissionCode1, delegerbarRettigheder.get(0).getRettighedskode().getKode());
    }
}
