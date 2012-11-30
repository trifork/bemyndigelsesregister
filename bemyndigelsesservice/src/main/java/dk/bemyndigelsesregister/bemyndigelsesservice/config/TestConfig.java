package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import com.googlecode.flyway.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 *
 */
@Profile("test")
@Configuration
public class TestConfig {

    @Bean(initMethod = "migrate")
    @DependsOn("flyway")
    public Flyway flywayTestData(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDisableInitCheck(true);
        flyway.setDataSource(dataSource);
        flyway.setBaseDir("db/testdata");
        flyway.setTable("testschema_version");
        return flyway;
    }

}
