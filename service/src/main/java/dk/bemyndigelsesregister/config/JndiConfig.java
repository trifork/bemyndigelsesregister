package dk.bemyndigelsesregister.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osjava.sj.MemoryContextFactory;
import org.osjava.sj.SimpleJndi;
import org.osjava.sj.jndi.MemoryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

@Configuration
public class JndiConfig {
    private static final Logger log = LogManager.getLogger(JndiConfig.class);

    @Value("${spring.datasource.bem.driver-class-name}")
    private String driverClassName;

    @Autowired
    public void bindDataSource(ResourceLoader resourceLoader,
                               @Qualifier("bemDataSource") DataSource bemDataSource,
                               @Qualifier("craDataSource") DataSource craDataSource) throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MemoryContextFactory.class.getName());
        System.setProperty(SimpleJndi.SHARED, "true");
        System.setProperty(MemoryContext.IGNORE_CLOSE, "true");

        if (driverClassName.contains("h2")) {
            log.info("Using H2 memory database for CRA");
            new InitialContext().rebind(getDatasource(resourceLoader), bemDataSource);
        } else {
            log.info("Using external CRA database");
            new InitialContext().rebind(getDatasource(resourceLoader), craDataSource);
        }
    }

    private String getDatasource(ResourceLoader resourceLoader) {
        Properties properties = fetchProperties("classpath:cra.properties", resourceLoader);
        return String.valueOf(properties.get("datasource"));
    }

    public static Properties fetchProperties(String resourceLocation, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(resourceLocation);
        try (InputStream in = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
