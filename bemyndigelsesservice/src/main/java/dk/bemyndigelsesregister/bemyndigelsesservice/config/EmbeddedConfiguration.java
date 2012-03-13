package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@Profile("embedded")
public class EmbeddedConfiguration {
    //private final Log logger = LogFactory.getLog(getClass());

    @Bean
    public DataSource dataSource() {
        //logger.info("Creating development database");
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }
}
