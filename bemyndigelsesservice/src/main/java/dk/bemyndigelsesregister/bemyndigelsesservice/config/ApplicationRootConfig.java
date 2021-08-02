package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager;
import com.googlecode.flyway.core.Flyway;
import dk.bemyndigelsesregister.bemyndigelsesservice.server.audit.AuditLogger;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.persistence.Entity;
import javax.sql.DataSource;
import java.util.ArrayList;

import static java.lang.System.getProperty;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.shared.service", "dk.bemyndigelsesregister.bemyndigelsesservice.server"})
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationRootConfig implements TransactionManagementConfigurer {
    private static Logger logger = Logger.getLogger(ApplicationRootConfig.class);

    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Value("${flyway.enabled}")
    private String flywayEnabled;
    @Value("${auditlog.enabled}")
    private String auditLogEnabled;
    @Value("${auditlog.usemock}")
    private String auditLogUseMock;

    @Bean
    public static PropertyPlaceholderConfigurer configuration() {
        final PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
        String bemyndigelseHome = getProperty("bemyndigelse.home");
        if (bemyndigelseHome != null) {
            logger.info("Loading properties from " + bemyndigelseHome);
            props.setLocations(new FileSystemResource(bemyndigelseHome));
        } else {
            logger.warn("bemyndigelse.home not set. Loading default configuration");
            props.setLocations(new ClassPathResource("bemyndigelse.properties"));
        }
        props.setIgnoreResourceNotFound(true);
        props.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return props;
    }

    @Bean
    public AuditLogger auditLogger() {
        return new AuditLogger(Boolean.valueOf(auditLogEnabled), Boolean.valueOf(auditLogUseMock));
    }
//
//    @Bean(initMethod = "migrate")
//    public Flyway flyway(DataSource dataSource) {
//        if (Boolean.valueOf(flywayEnabled)) {
//            Flyway flyway = new Flyway();
//            flyway.setDataSource(dataSource);
//            flyway.setCleanOnValidationError(false);
//            return flyway;
//        } else {
//            logger.info("Skipped FlyWay");
//            return null;
//        }
//    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        dataSource.setMinEvictableIdleTimeMillis(3 * 60 * 1000);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }

    @Bean
    public EbeanServerFactoryBean ebeanServer(DataSource dataSource) throws Exception {
        final EbeanServerFactoryBean factoryBean = new EbeanServerFactoryBean();
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("localhostConfig");
        serverConfig.setClasses(new ArrayList<Class<?>>(new Reflections("dk.bemyndigelsesregister.bemyndigelsesservice.domain").getTypesAnnotatedWith(Entity.class)));
        serverConfig.setDataSource(dataSource);
        serverConfig.setExternalTransactionManager(new SpringAwareJdbcTransactionManager());
        factoryBean.setServerConfig(serverConfig);
        return factoryBean;
    }

    @Bean(name = {"serviceMarshaller", "serviceUnmarshaller"})
    @Primary
    public Jaxb2Marshaller serviceMarshaller() {
        final Jaxb2Marshaller bean = new Jaxb2Marshaller();
        bean.setContextPaths(
                "dk.nsi.bemyndigelse._2016._01._01",
                "dk.nsi.bemyndigelse._2017._08._01",
                "dk.medcom.dgws._2006._04.dgws_1_0",
//                "org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0",
//                "org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_utility_1_0",
//                "org.w3._2000._09.xmldsig",
//                "oasis.names.tc.saml._2_0.assertion",
                "dk.oio.rep.cpr_dk.xml.schemas.core._2005._03._18",
                "dk.oio.rep.cvr_dk.xml.schemas._2005._03._22"
        );
        return bean;
    }

    @Bean(name = {"nspMarshaller", "nspUnarshaller"})
    public Jaxb2Marshaller nspMarshaller() {
        final Jaxb2Marshaller bean = new Jaxb2Marshaller();
        bean.setContextPath("dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel");
        return bean;
    }
}
