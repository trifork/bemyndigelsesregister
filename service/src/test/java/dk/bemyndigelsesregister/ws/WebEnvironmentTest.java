package dk.bemyndigelsesregister.ws;

import dk.sosi.seal.model.AuthenticationLevel;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class WebEnvironmentTest {
    @LocalServerPort
    private int port;

    private final SosiUtil sosiUtil;

    protected enum CallMode {
        PLAIN, DGWS_LEVEL_3, DGWS_LEVEL_4, IDWS
    }

    protected WebEnvironmentTest() {
        try {
            this.sosiUtil = new SosiUtil();
        } catch (Exception ex) {
            throw new RuntimeException("Error initializing SosiUtil", ex);
        }
    }

    protected URL getUrl(String path) throws MalformedURLException {
        return new URL("http://localhost:" + port + "/" + path);
    }

    protected String httpWrite(String soapAction, String xmldata, CallMode callMode) throws Exception {
        System.out.println("Request: " + xmldata);

        if (callMode == CallMode.DGWS_LEVEL_3 || callMode == CallMode.DGWS_LEVEL_4) { // transform to DGWS message
            ByteArrayInputStream bais = new ByteArrayInputStream(xmldata.getBytes(StandardCharsets.UTF_8));
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, bais);
            sosiUtil.addSoapHeader(soapMessage, callMode == CallMode.DGWS_LEVEL_4 ? AuthenticationLevel.MOCES_TRUSTED_USER : AuthenticationLevel.VOCES_TRUSTED_SYSTEM);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo(baos);
            xmldata = baos.toString(StandardCharsets.UTF_8.name());
        }

        HttpClient httpClient = HttpClientBuilder.create().build();

        String endpoint = getUrl(callMode == CallMode.PLAIN ? "bem/u" : "/bem").toString();
        System.out.println(" Calling " + endpoint + ", SOAPAction=" + soapAction);
        HttpPost request = new HttpPost(endpoint);
        request.addHeader("SOAPAction", "\"" + soapAction + "\"");

        StringEntity stringEntity = new StringEntity(xmldata, StandardCharsets.UTF_8);
        stringEntity.setContentType("text/xml");
        request.setEntity(stringEntity);

        HttpResponse response = httpClient.execute(request);

        System.out.println("Response: " + response);

        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    protected String loadXML(String resourceName) throws IOException, SOAPException {
        InputStream stream = getClass().getResourceAsStream(resourceName);
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, stream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapMessage.writeTo(baos);
        return baos.toString(StandardCharsets.UTF_8.name());
    }

}
