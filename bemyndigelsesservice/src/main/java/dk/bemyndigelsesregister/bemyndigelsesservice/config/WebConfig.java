package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import dk.bemyndigelsesregister.bemyndigelsesservice.web.BemyndigelsesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.inject.Inject;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.bemyndigelsesservice.web", "dk.bemyndigelsesregister.shared.web"})
public class WebConfig extends WebMvcConfigurationSupport {
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
