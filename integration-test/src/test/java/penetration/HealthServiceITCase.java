package penetration;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.net.*;

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix;
import static org.junit.Assert.*;

public class HealthServiceITCase {

    @Test
    public void canAccessBemyndigelsesserviceHealth() throws Exception {
        URLConnection urlConnection = new URL(urlPrefix() +  "/bemyndigelsesservice/health").openConnection();
        assertEquals("OK", IOUtils.toString(urlConnection.getInputStream()));
    }

    @Test
    public void canAccessStamdataserviceHealth() throws Exception {
        URLConnection urlConnection = new URL(urlPrefix() + "/stamdataservice/health").openConnection();
        assertEquals("OK", IOUtils.toString(urlConnection.getInputStream()));
    }
}
