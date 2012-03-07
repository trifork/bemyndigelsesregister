package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.inject.Inject;

@Configuration
@ComponentScan("dk.bemyndigelsesregister.bemyndigelsesservice.web")
public class WebConfig extends WebMvcConfigurationSupport {
    @Inject
    ApplicationRootConfig applicationRootConfig;
}
