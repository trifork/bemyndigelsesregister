package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by obj on 05-02-2016.
 */
public class SystemDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindSystemByDomainId() throws Exception {
        System.setProperty("ebean.debug.sql", "true");

        String domainId = "triforktest";
        DelegatingSystem system = delegatingSystemDao.findByDomainId(domainId);

        System.out.println(system);
        assertEquals("findByDomainId should return an object with correct domainId", domainId, system.getDomainId());
    }
}
