package dk.bemyndigelsesregister.bemyndigelsesservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import com.trifork.dgws.annotations.EnableDgwsProtection;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Properties;

@Configuration
@ComponentScan({"dk.bemyndigelsesregister.bemyndigelsesservice.web", "dk.bemyndigelsesregister.shared.web"})
@EnableDgwsProtection(skipSOSI="${sosi.skipSosi}", test="${sosi.test}")
public class WebConfig extends WebMvcConfigurationSupport {
    @Inject
    ApplicationRootConfig applicationRootConfig;

    @Bean
    public WsdlDefinition serviceDefinition() {
        final DefaultWsdl11Definition bean = new DefaultWsdl11Definition();
        bean.setSchema(schema1XsdSchema());
        //bean.setSchemaCollection(schemaCollection()); //this will inline the defs from the schemas in the collections
        bean.setPortTypeName("BemyndigelsesService");
        bean.setLocationUri("http://localhost:8080/BemyndigelsesService");
        Properties soapActions = new Properties();
        soapActions.put("SletBemyndigelser","http://nsi.dk/bemyndigelse/2012/05/01/SletBemyndigelser");
        soapActions.put("OpretAnmodningOmBemyndigelser","http://nsi.dk/bemyndigelse/2012/05/01/OpretAnmodningOmBemyndigelser");
        soapActions.put("OpretGodkendteBemyndigelser","http://nsi.dk/bemyndigelse/2012/05/01/OpretGodkendteBemyndigelser");
        soapActions.put("HentBemyndigelser","http://nsi.dk/bemyndigelse/2012/05/01/HentBemyndigelser");
        soapActions.put("HentMetadata","http://nsi.dk/bemyndigelse/2012/05/01/HentMetadata");
        soapActions.put("IndlaesMetadata","http://nsi.dk/bemyndigelse/2012/05/01/IndlaesMetadata");
        soapActions.put("GodkendBemyndigelse","http://nsi.dk/bemyndigelse/2012/05/01/GodkendBemyndigelse");
        soapActions.put("GetDelegations","http://nsi.dk/bemyndigelse/2016/01/01/GetDelegations");
        soapActions.put("CreateDelegations","http://nsi.dk/bemyndigelse/2016/01/01/CreateDelegations");
        soapActions.put("DeleteDelegations","http://nsi.dk/bemyndigelse/2016/01/01/DeleteDelegations");
        soapActions.put("GetMetadata","http://nsi.dk/bemyndigelse/2016/01/01/GetMetadata");
        soapActions.put("PutMetadata","http://nsi.dk/bemyndigelse/2016/01/01/PutMetadata");
        bean.setSoapActions(soapActions);
        return bean;
    }

    @Bean
    public CommonsXsdSchemaCollection schemaCollection() {
        final Resource[] resources = {
                new ClassPathResource("/schema/CPR_PersonCivilRegistrationIdentifier.xsd"),
                new ClassPathResource("/schema/CVR_CVRnumberIdentifier.xsd"),
                new ClassPathResource("/schema/bemyndigelsesservice.xsd"),
                new ClassPathResource("/schema/2016_01_01/bemyndigelsesservice.xsd")
        };
        for (Resource resource : resources) {
            if (!resource.exists()) {
                throw new RuntimeException("Resource not found: " + resource.getDescription());
            }
        }
        return new CommonsXsdSchemaCollection(resources);
    }

    @Bean
    public SimpleXsdSchema schema1XsdSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/2016_01_01/bemyndigelsesservice.xsd"));
    }

    @Bean
    public WsdlDefinitionHandlerAdapter wsdlDefinitionHandlerAdapter() {
        WsdlDefinitionHandlerAdapter wsdlDefinitionHandlerAdapter = new WsdlDefinitionHandlerAdapter();
        wsdlDefinitionHandlerAdapter.setTransformLocations(true);
        return wsdlDefinitionHandlerAdapter;
    }


    /**
     * Serve XSD files as static resources - this will cause a SimpleUrlHandlerMapping for *.xsd to be created
     * The order of this must be lower than the order for the RequestMappingHandlerMapping bean.
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(0);
        registry.addResourceHandler("*.xsd").addResourceLocations("classpath:/schema/");
    }


    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setInterceptors(getInterceptors());
        return handlerMapping;
    }


    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(2);
        final HashMap<String, Object> urlMap = new HashMap<String, Object>();
        urlMap.put("*.wsdl", serviceDefinition());
        mapping.setUrlMap(urlMap);
        return mapping;
    }

    @Override
    @Bean
    public BeanNameUrlHandlerMapping beanNameHandlerMapping() {
        final BeanNameUrlHandlerMapping mapping = super.beanNameHandlerMapping();
        mapping.setOrder(3);
        mapping.setDefaultHandler(soapMessageDispatcher());
        return mapping;
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
    public WebServiceMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public EndpointMapping endpointMapping(EndpointInterceptor[] endpointInterceptors) {
        final PayloadRootAnnotationMethodEndpointMapping mapping = new PayloadRootAnnotationMethodEndpointMapping();
        mapping.setInterceptors(endpointInterceptors);
        return mapping;
    }

    @Bean
    public EndpointInterceptor SoapEnvelopeEndpointInterceptor() {
        return new SoapEnvelopeLoggingInterceptor();
    }

    @Bean
    public EndpointInterceptor payloadValidationEndpointInterceptor() {
        final PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();
        interceptor.setSchemas(new Resource[]{
                new ClassPathResource("schema/2016_01_01/bemyndigelsesservice.xsd"),
        });
        interceptor.setValidateRequest(true);
        interceptor.setValidateResponse(false);
        return interceptor;
    }
}
