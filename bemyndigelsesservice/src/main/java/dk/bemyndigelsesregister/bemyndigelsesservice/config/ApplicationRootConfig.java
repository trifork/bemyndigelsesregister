package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.googlecode.flyway.core.Flyway;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.persistence.Entity;
import javax.sql.DataSource;
import java.util.ArrayList;

import static java.lang.System.getProperty;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.shared.service", "dk.bemyndigelsesregister.bemyndigelsesservice.server"})
@EnableScheduling
public class ApplicationRootConfig {

    @Bean
    public static PropertyPlaceholderConfigurer configuration() {
        final PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
        props.setLocations(new Resource[]{
                new ClassPathResource("default.properties"),
                new ClassPathResource("jdbc.default.properties"),
                new ClassPathResource("jdbc." + getProperty("user.name") + ".properties"),
                new FileSystemResource(getProperty("user.home") + "/.bemyndigelsesservice/passwords.properties")
        });
        props.setIgnoreResourceNotFound(true);
        props.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return props;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        //TODO: when http://code.google.com/p/flyway/issues/detail?id=174 is finished, we want to use multiple paths for migration in test
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

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

    @Bean
    public DataSource dataSource(@Value("${jdbc.url}") String url, @Value("${jdbc.username}") String username, @Value("${jdbc.password}") String password) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(
                url,
                username,
                password
        );
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public EbeanServerFactoryBean ebeanServer(DataSource dataSource) throws Exception {
        final EbeanServerFactoryBean factoryBean = new EbeanServerFactoryBean();
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("localhostConfig");
        serverConfig.setClasses(new ArrayList<Class<?>>(new Reflections("dk.bemyndigelsesregister.bemyndigelsesservice.domain").getTypesAnnotatedWith(Entity.class)));
        serverConfig.setDataSource(dataSource);
        factoryBean.setServerConfig(serverConfig);
        return factoryBean;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        final Jaxb2Marshaller bean = new Jaxb2Marshaller();
        bean.setContextPaths(
                "dk.bemyndigelsesregister.bemyndigelsesservice.web.request",
                "dk.bemyndigelsesregister.bemyndigelsesservice.web.response",
                "dk.medcom.dgws._2006._04.dgws_1_0"
        );
        return bean;
    }

}
