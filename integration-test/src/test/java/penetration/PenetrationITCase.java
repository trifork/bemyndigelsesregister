package penetration;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.net.*;

import static org.junit.Assert.*;

public class PenetrationITCase {

    @Test
    public void canAccessBemyndigelsesserviceHealth() throws Exception {
        URLConnection urlConnection = new URL("http://localhost:8080/bemyndigelsesservice/health").openConnection();
        assertEquals("OK", IOUtils.toString(urlConnection.getInputStream()));
    }

    @Test
    public void canAccessStamdataserviceHealth() throws Exception {
        URLConnection urlConnection = new URL("http://localhost:8080/stamdataservice/health").openConnection();
        assertEquals("OK", IOUtils.toString(urlConnection.getInputStream()));
    }
}
