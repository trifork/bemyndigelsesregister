package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServletITCase {
    private static final String urlPrefix = "http://localhost:8087/bem";

    @Test
    public void canAccessHealthServlet() throws IOException {
        URLConnection urlConnection = new URL(urlPrefix + "/health").openConnection();
        assertEquals("OK", IOUtils.toString(urlConnection.getInputStream()));
    }

    @Test
    public void canAccessVersionServlet() throws IOException {
        URLConnection urlConnection = new URL(urlPrefix + "/health/version").openConnection();
        assertEquals("develop", IOUtils.toString(urlConnection.getInputStream()));
    }

    @Test
    public void canAccessDKSServlet() throws IOException {
        URLConnection urlConnection = new URL(urlPrefix + "/dksconfig").openConnection();
        String content = IOUtils.toString(urlConnection.getInputStream());
        assertTrue(content.contains("dksVersion"));
    }
}
