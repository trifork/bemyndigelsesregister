package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by obj on 05-02-2016.
 */
public class DomainDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void testFindDomain() {
        String domainId = "trifork-test";

        Domain domain = domainDao.findByDomainId(domainId);
        assertEquals("findByDomainId should return a domain object with correct domainId", domainId, domain.getDomainId());
    }

}
