package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.avaje.ebean.Ebean;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.DelegatingSystem;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Role;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by obj on 05-02-2016.
 */
public class RoleDaoEbeanTest extends DaoUnitTestSupport {
    @Test
    public void testFindRoleByDomainId() throws Exception {
        System.setProperty("ebean.debug.sql", "true");

        DelegatingSystem system = delegatingSystemDao.findByDomainId("triforktest");
        String domainId = "Laege";

        List<Role> roles = roleDao.findBySystem(system.getId());
        System.out.println(roles);
//        assertEquals("findByDomainId should return a domain object with correct domainId", domainId, role.getDomainId());
    }

}
