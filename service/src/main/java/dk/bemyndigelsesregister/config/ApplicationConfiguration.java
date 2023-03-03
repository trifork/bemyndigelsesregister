package dk.bemyndigelsesregister.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    @ConfigurationProperties("spring.datasource.bem")
    public DataSourceProperties bemDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.cra")
    public DataSourceProperties craDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name="bemDataSource")
    @FlywayDataSource
    @ConfigurationProperties("spring.datasource.bem")
    public DataSource bemDataSource() {
        //return bemDataSourceProperties().initializeDataSourceBuilder().build();
        return DataSourceBuilder.create().build();
    }

    @Bean(name="craDataSource")
    @ConfigurationProperties("spring.datasource.cra")
    public DataSource craDataSource() {
        //return craDataSourceProperties().initializeDataSourceBuilder().build();
        return DataSourceBuilder.create().build();
    }

    @Bean
    public TransactionManager transactionManager(@Qualifier("bemDataSource") DataSource bemDataSource) {
        return new DataSourceTransactionManager(bemDataSource);
    }
}
