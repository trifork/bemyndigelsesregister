package dk.bemyndigelsesregister.dao;

import dk.bemyndigelsesregister.dao.SystemVariableDAO;
import dk.bemyndigelsesregister.domain.SystemVariable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SystemVariableDAOTest {

    @Autowired
    private SystemVariableDAO systemVariableDAO;

    @Test
    public void canReadGetVariableByName() {
        assertEquals("den gode test", systemVariableDAO.getByName("testVariable").getValue());
    }

    @Test
    public void canUpdateVariable() {
        SystemVariable sv = systemVariableDAO.getByName("testVariable");
        sv.setValue("Den Rigtigt Gode Test");
        systemVariableDAO.save(sv);

        assertEquals("Den Rigtigt Gode Test", systemVariableDAO.getByName("testVariable").getValue());
        sv.setValue("den gode test");
        systemVariableDAO.save(sv);
        assertEquals("den gode test", systemVariableDAO.getByName("testVariable").getValue());
    }
}
