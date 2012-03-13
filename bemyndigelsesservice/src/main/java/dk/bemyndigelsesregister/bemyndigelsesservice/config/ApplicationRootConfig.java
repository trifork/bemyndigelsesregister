package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import com.googlecode.flyway.core.Flyway;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

import static java.lang.System.getProperty;

@Configuration
@ComponentScan("dk.bemyndigelsesregister.shared.service")
public class ApplicationRootConfig {

    @Bean
    public PropertyPlaceholderConfigurer configuration() {
        final PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
        props.setLocations(new Resource[]{
                new ClassPathResource("default.properties"),
                new ClassPathResource("jdbc.default.properties"),
                //not sure whether we will need this: new ClassPathResource("jdbc." + getProperty("user.name") + ".properties"),
                new FileSystemResource(getProperty("user.home") + "/.bemyndigelsesservice/passwords.properties")
        });
        props.setIgnoreResourceNotFound(true);
        return props;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }
}
