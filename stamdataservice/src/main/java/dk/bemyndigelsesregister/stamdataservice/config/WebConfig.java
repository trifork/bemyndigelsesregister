package dk.bemyndigelsesregister.stamdataservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.inject.Inject;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.stamdataservice.web", "dk.bemyndigelsesregister.shared.web"})
public class WebConfig extends WebMvcConfigurationSupport {
    @Inject
    ApplicationRootConfig applicationRootConfig;
}
