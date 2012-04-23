package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.googlecode.flyway.core.Flyway;
import dk.bemyndigelsesregister.bemyndigelsesservice.config.ApplicationRootConfig;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationRootConfig.class, DaoUnitTestSupport.MockContext.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class DaoUnitTestSupport extends AbstractJUnit4SpringContextTests {
    @Inject
    Flyway flyway;

    public static class MockContext {
        @Bean
        public ServletContext servletContext() {
            return mock(ServletContext.class);
        }

        @Bean
        public PropertyPlaceholderConfigurer configuration() {
            final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setLocations(new Resource[] {
                    new ClassPathResource("default.properties"),
                    new ClassPathResource("jdbc.unittest.properties")
            });
            return configurer;
        }
    }

    @After
    public void tearDown() throws Exception {
        flyway.clean();
    }
}
