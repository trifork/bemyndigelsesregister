package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Domain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DomainDaoEbeanTest extends DaoUnitTestSupport {

    @Test
    public void testFindDomain() {
        String domainCode = "trifork-test";

        Domain domain = domainDao.findByCode(domainCode);
        assertEquals("findByCode should return a domain object with correct code", domainCode, domain.getCode());
    }
}
