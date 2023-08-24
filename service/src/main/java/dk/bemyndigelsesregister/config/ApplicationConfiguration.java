package dk.bemyndigelsesregister.config;

import dk.sds.nsp.accesshandler.config.NspSecurityProtocolHandlerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableScheduling
public class ApplicationConfiguration {
    @Value("${signing.keystore.filename}")
    private String signingKeystoreFilename;

    @Value("${signing.keystore.password}")
    private String signingKeystorePassword;

    @Value("${signing.keystore.alias}")
    private String signingKeystoreAlias;

    @PostConstruct
    private void init() {
        NspSecurityProtocolHandlerConfiguration.signingKeystoreFilename = signingKeystoreFilename;
        NspSecurityProtocolHandlerConfiguration.signingKeystorePassword = signingKeystorePassword;
        NspSecurityProtocolHandlerConfiguration.signingKeystoreAlias = signingKeystoreAlias;
    }

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
