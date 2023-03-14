package dk.bemyndigelsesregister.ws;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HealthControllerTest extends WebEnvironmentTest {
    @Test
    public void testDKSServlet() throws IOException {
        URLConnection urlConnection = getUrl("actuator/health").openConnection();
        String content = IOUtils.toString(urlConnection.getInputStream(), StandardCharsets.UTF_8);
        assertTrue(content.contains("\"status\":\"UP\""));
    }
}
