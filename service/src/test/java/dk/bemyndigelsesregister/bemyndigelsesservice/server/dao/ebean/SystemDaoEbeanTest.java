package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SystemDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindSystemByCode() {
        DelegatingSystem system = delegatingSystemDao.findByCode(TestData.systemCode);
        System.out.println(system);

        assertEquals("findByCode should return an object with correct code", TestData.systemCode, system.getCode());
        assertEquals("findByCode should return an object with correct description", TestData.systemDescription, system.getDescription());
    }
}
