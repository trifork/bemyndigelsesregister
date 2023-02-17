package dk.bemyndigelsesregister.ws;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DKSControllerTest {
    @LocalServerPort
    private int port;

    @Test
    public void testDKSServlet() throws IOException {
        URLConnection urlConnection = getUrl("dksconfig").openConnection();
        String content = IOUtils.toString(urlConnection.getInputStream(), StandardCharsets.UTF_8);
        assertTrue(content.contains("dksVersion"));
    }

    private URL getUrl(String path) throws MalformedURLException {
        return new URL("http://localhost:" + port + "/" + path);
    }
}
