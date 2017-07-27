package dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.ebean;

import com.trifork.dgws.DgwsRequestContext;
import dk.bemyndigelsesregister.bemyndigelsesservice.config.ApplicationRootConfig;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationRootConfig.class, DaoUnitTestSupport.MockContext.class})
public abstract class DaoUnitTestSupport extends AbstractJUnit4SpringContextTests {
    @Inject
    DomainDao domainDao;
    @Inject
    RoleDao roleDao;
    @Inject
    SystemDao systemDao;
    @Inject
    PermissionDao permissionDao;
    @Inject
    DelegationPermissionDao delegationPermissionDao;
    @Inject
    DelegatingSystemDao delegatingSystemDao;

    @Inject
    WhitelistDao whitelistDao;

    public static class MockContext {
        @Bean
        public ServletContext servletContext() {
            return mock(ServletContext.class);
        }

        @Bean
        public DgwsRequestContext dgwsRequestContext() {
            return mock(DgwsRequestContext.class);
        }

        @Bean
        public PropertyPlaceholderConfigurer configuration() {
            final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setLocations(new Resource[]{
                    new ClassPathResource("bemyndigelse.properties"),
                    new ClassPathResource("bemyndigelse.test.properties")
            });
            return configurer;
        }
    }

}
