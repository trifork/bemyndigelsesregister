package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.dao.WhitelistDAO;
import dk.bemyndigelsesregister.domain.WhitelistType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WhitelistDAOTest {
    @Autowired
    private WhitelistDAO whitelistDAO;

    @Test
    public void canFindExisting() {
        assertTrue(whitelistDAO.exists("test", WhitelistType.SYSTEM_CVR, "1"));
        assertFalse(whitelistDAO.exists("test2", WhitelistType.SYSTEM_CVR, "1"));
        assertFalse(whitelistDAO.exists("test", WhitelistType.USER_CVR_CPR, "1"));
    }
}