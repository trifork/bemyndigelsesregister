package dk.bemyndigelsesregister.ws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * DCC konfigurationsservice (DKS)
 */
@Controller
@RequestMapping("/")
public class DKSController {
    private static final Logger log = LogManager.getLogger(DKSController.class);

    private static final String RESOURCE_NAME = "/dksconfig.xml";
    private static final String Instant_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String ENDPOINT_PLACEHOLDER = "ENDPOINT_PLACEHOLDER";

    @Value("${dcc.endpoint}")
    private String dccEndpoint;

    @RequestMapping(value = "/dksconfig", produces = "application/xml")
    @ResponseBody
    public ResponseEntity<String> dksConfig() throws IOException {
        log.debug("dksconfig request");

        URL url = getClass().getResource(RESOURCE_NAME);
        String filetimestamp = new SimpleDateFormat(Instant_FORMAT).format(url.openConnection().getLastModified());

        InputStream in = getClass().getResourceAsStream(RESOURCE_NAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");

        return ResponseEntity.ok().headers(headers).body(getStringWithResolvedVariables(out, filetimestamp));
    }

    private String getStringWithResolvedVariables(StringBuilder template, String filetimestamp) {
        int timestampIndex = template.indexOf(TIMESTAMP);
        template.replace(timestampIndex, timestampIndex + TIMESTAMP.length(), filetimestamp);

        int endpointIndex = template.indexOf(ENDPOINT_PLACEHOLDER);
        template.replace(endpointIndex, endpointIndex + ENDPOINT_PLACEHOLDER.length(), dccEndpoint);

        return template.toString();
    }
}
