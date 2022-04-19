package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DelegatingSystemDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindDelegatingSystem() {
        DelegatingSystem system = delegatingSystemDao.get(1);
        System.out.println(system);

        assertNotNull(system);
        assertEquals("DelegatingSystem should contain correct code", TestData.systemCode, system.getCode());
        assertEquals("DelegatingSystem should contain correct description", TestData.systemDescription, system.getDescription());
    }

    @Test
    public void testFindDelegatingSystemByCode() {
        DelegatingSystem system = delegatingSystemDao.findByCode(TestData.systemCode);
        System.out.println(system);

        assertNotNull(system);
        assertEquals("DelegatingSystem should contain correct code", TestData.systemCode, system.getCode());
        assertEquals("DelegatingSystem should contain correct description", TestData.systemDescription, system.getDescription());
    }
}
