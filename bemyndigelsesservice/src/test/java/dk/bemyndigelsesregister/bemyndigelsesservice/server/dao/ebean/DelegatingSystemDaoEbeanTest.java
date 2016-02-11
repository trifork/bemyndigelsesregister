package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
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
    }

    @Test
    public void testFindDelegatingSystemByDomainId() {
        String kode = "testsys";

        DelegatingSystem system = delegatingSystemDao.findByDomainId(kode);
        System.out.println(system);
        assertEquals("DelegatingSystem should contain correct code", kode, system.getDomainId());
    }
}
