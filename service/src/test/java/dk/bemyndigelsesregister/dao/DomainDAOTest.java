package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.dao.DomainDAO;
import dk.bemyndigelsesregister.domain.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class DomainDAOTest {

    @Autowired
    private DomainDAO domainDAO;

    @Test
    public void testFindDomain() {
        String domainCode = "Trifork";

        Domain domain = domainDAO.findByCode(domainCode);
        assertEquals(domainCode, domain.getCode());
    }
}
