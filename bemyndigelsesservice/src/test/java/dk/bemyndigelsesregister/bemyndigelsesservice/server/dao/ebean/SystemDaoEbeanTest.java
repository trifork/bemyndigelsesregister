package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.TestData;
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
        DelegatingSystem system = delegatingSystemDao.findByDomainId(TestData.systemCode);
        System.out.println(system);

        assertEquals("findByDomainId should return an object with correct domainId", TestData.systemCode, system.getDomainId());
        assertEquals("findByDomainId should return an object with correct description", TestData.systemDescription, system.getDescription());
    }
}
