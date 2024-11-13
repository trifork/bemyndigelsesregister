package dk.bemyndigelsesregister.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(WebServiceConfig.class);
    private final ClassPathResource xsdSchemaResource = new ClassPathResource("/schema/2017_08_01/bemyndigelsesservice.xsd");
    private final ClassPathResource dumpRestoreXsdSchemaResource = new ClassPathResource("/schema/2013/01/01/DumpRestore.xsd");

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/bem/*", "/dumprestore/*");
    }

    @Bean
    public Wsdl11Definition delegationWsdlDefinition(XsdSchema delegationServiceSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("DelegationPort");
        wsdl11Definition.setLocationUri("/bem");
        wsdl11Definition.setTargetNamespace("http://nsi.dk/bemyndigelse/2017/08/01");
        wsdl11Definition.setSchema(delegationServiceSchema);
        return wsdl11Definition;
    }

    @Bean
    public Wsdl11Definition dumpRestoreWsdlDefinition(XsdSchema dumpRestoreSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("DumpRestorePort");
        wsdl11Definition.setLocationUri("/dumprestore");
        wsdl11Definition.setTargetNamespace("http://www.ssi.dk/nsi/xml.schema/2013/01/01");
        wsdl11Definition.setSchema(dumpRestoreSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema delegationServiceSchema() {
        return new SimpleXsdSchema(xsdSchemaResource);
    }

    @Bean
    public XsdSchema dumpRestoreSchema() {
        return new SimpleXsdSchema(dumpRestoreXsdSchemaResource);
    }

    @Bean
    public XsdSchemaCollection schemaCollection() {
        return new XsdSchemaCollection() {
            @Override
            public XsdSchema[] getXsdSchemas() {
                return new XsdSchema[]{delegationServiceSchema(), dumpRestoreSchema()};
            }

            @Override
            public XmlValidator createValidator() {
                try {
                    Resource[] schemas = new Resource[]{xsdSchemaResource, dumpRestoreXsdSchemaResource};
                    return XmlValidatorFactory.createValidator(schemas, XmlValidatorFactory.SCHEMA_W3C_XML);
                } catch (IOException e) {
                    log.error("Error creating schema validators", e);
                }
                return null;
            }
        };
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.setXsdSchemaCollection(schemaCollection());
        interceptors.add(validatingInterceptor);

        super.addInterceptors(interceptors);
    }
}
