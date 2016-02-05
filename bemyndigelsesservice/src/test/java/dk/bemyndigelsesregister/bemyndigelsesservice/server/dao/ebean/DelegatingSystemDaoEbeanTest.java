package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by obj on 05-02-2016.
 */
public class DelegatingSystemDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindDelegatingSystem() {
        String kode = "triforktest";

        DelegatingSystem system = delegatingSystemDao.findByDomainId(kode);
        System.out.println(system);
        assertEquals("DelegatingSystem should contain correct code", kode, system.getDomainId());
    }
}
