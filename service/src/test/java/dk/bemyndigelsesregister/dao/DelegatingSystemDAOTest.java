package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.dao.DelegatingSystemDAO;
import dk.bemyndigelsesregister.dao.TestData;
import dk.bemyndigelsesregister.domain.DelegatingSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DelegatingSystemDAOTest {
    @Autowired
    private DelegatingSystemDAO delegatingSystemDAO;

    @Test
    public void testFindDelegatingSystem() {
        DelegatingSystem system = delegatingSystemDAO.get(1);
        System.out.println(system);

        assertNotNull(system);
        assertEquals(TestData.systemCode, system.getCode());
        assertEquals(TestData.systemDescription, system.getDescription());
    }

    @Test
    public void testFindDelegatingSystemByCode() {
        DelegatingSystem system = delegatingSystemDAO.findByCode(TestData.systemCode);
        System.out.println(system);

        assertNotNull(system);
        assertEquals(TestData.systemCode, system.getCode());
        assertEquals(TestData.systemDescription, system.getDescription());
    }
}
