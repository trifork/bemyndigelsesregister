package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Configuration
@Profile("embedded")
public class EmbeddedConfiguration {
    private static Logger logger = Logger.getLogger(EmbeddedConfiguration.class);

    @Value("${jdbc.password}")
    String jdbcPassword;

    @Bean
    public DataSource dataSource() {
        if (isNotEmpty(jdbcPassword)) {
            logger.warn("jdbc.password is set, but application profile is embedded. You probably want to start container with system property spring.profiles.active=live");
        }
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }
}
