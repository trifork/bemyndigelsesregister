package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import dk.bemyndigelsesregister.bemyndigelsesservice.web.BemyndigelsesService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.remoting.jaxws.LocalJaxWsServiceFactoryBean;
import org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import javax.inject.Inject;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.bemyndigelsesservice.web", "dk.bemyndigelsesregister.shared.web"})
public class WebConfig extends WebMvcConfigurationSupport {
    private static Logger logger = Logger.getLogger(WebConfig.class);
    @Inject
    ApplicationRootConfig applicationRootConfig;


    @Bean
    public SimpleJaxWsServiceExporter simpleJaxWsServiceExporter() {
        final SimpleJaxWsServiceExporter serviceExporter = new SimpleJaxWsServiceExporter();
        serviceExporter.setBaseAddress("http://localhost:8080/bemyndigelsesservice/");
        return serviceExporter;
    }

    @Bean
    public BemyndigelsesService bemyndigelsesService() {
        return new BemyndigelsesService();
    }
}
