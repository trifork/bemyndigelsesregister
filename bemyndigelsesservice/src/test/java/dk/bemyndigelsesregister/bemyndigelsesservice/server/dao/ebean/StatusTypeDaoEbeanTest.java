package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import dk.bemyndigelsesregister.bemyndigelsesservice.config.ApplicationRootConfig;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.StatusType;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.StatusTypeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationRootConfig.class, StatusTypeDaoEbeanTest.MockContext.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatusTypeDaoEbeanTest extends AbstractJUnit4SpringContextTests {
    public static class MockContext {
        @Bean
        public ServletContext servletContext() {
            return mock(ServletContext.class);
        }
    }

    @Inject
    StatusTypeDao dao;

    @Test
    public void springIsWorking() throws Exception {
        assertNotNull(dao);
    }

    @Test
    public void canGetById() throws Exception {
        StatusType statusType = dao.get(1l);
        assertNotNull(statusType);
        assertEquals("OK", statusType.getStatus());
    }
}
