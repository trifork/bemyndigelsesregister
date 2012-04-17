package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;

import javax.inject.Inject;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.bemyndigelsesservice.web", "dk.bemyndigelsesregister.shared.web"})
@ImportResource({"classpath:/dk/trifork/dgws/dgws-protection.xml"})
public class WebConfig extends WebMvcConfigurationSupport {
    @Inject
    ApplicationRootConfig applicationRootConfig;

    @Bean
    public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping(SoapMessageDispatcher soapMessageDispatcher) {
        final DefaultAnnotationHandlerMapping bean = new DefaultAnnotationHandlerMapping();
        bean.setOrder(1);
        bean.setDefaultHandler(soapMessageDispatcher);
        return bean;
    }

    @Bean
    public WebServiceMessageReceiverHandlerAdapter webServiceMessageReceiverHandlerAdapter(WebServiceMessageFactory messageFactory) {
        final WebServiceMessageReceiverHandlerAdapter bean = new WebServiceMessageReceiverHandlerAdapter();
        bean.setMessageFactory(messageFactory);
        return bean;
    }

    @Bean
    public SoapMessageDispatcher soapMessageDispatcher() {
        return new SoapMessageDispatcher();
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        final Jaxb2Marshaller bean = new Jaxb2Marshaller();
        bean.setContextPaths(
                "dk.bemyndigelsesregister.bemyndigelsesservice.web.request",
                "dk.bemyndigelsesregister.bemyndigelsesservice.web.response"
        );
        return bean;
    }

    @Bean
    public WebServiceMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }
}
