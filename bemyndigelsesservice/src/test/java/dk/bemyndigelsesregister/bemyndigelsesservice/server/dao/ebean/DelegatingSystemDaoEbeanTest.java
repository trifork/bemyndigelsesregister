package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by obj on 05-02-2016.
 */
public class DelegatingSystemDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindDelegatingSystem() {
        DelegatingSystem system = delegatingSystemDao.get(1);
        System.out.println(system);

        assertNotNull(system);
        assertEquals("DelegatingSystem should contain correct code", TestData.systemCode, system.getDomainId());
        assertEquals("DelegatingSystem should contain correct description", TestData.systemDescription, system.getDescription());
    }

    @Test
    public void testFindDelegatingSystemByDomainId() {

        DelegatingSystem system = delegatingSystemDao.findByDomainId(TestData.systemCode);
        System.out.println(system);

        assertNotNull(system);
        assertEquals("DelegatingSystem should contain correct code", TestData.systemCode, system.getDomainId());
        assertEquals("DelegatingSystem should contain correct description", TestData.systemDescription, system.getDescription());
    }
}
