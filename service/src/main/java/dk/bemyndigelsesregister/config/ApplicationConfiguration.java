package dk.bemyndigelsesregister.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableScheduling
public class ApplicationConfiguration {

    @Autowired
    public void setSystemProperties() {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    @Bean(name="bemDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.bem")
    @FlywayDataSource
    public DataSource bemDataSource() {
        return DataSourceBuilder.create().build();
    }

    @ConfigurationProperties(prefix = "spring.datasource.cra")
    @Bean(name="craDataSource")
    public DataSource craDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public TransactionManager transactionManager(@Qualifier("bemDataSource") DataSource bemDataSource) {
        return new DataSourceTransactionManager(bemDataSource);
    }
}