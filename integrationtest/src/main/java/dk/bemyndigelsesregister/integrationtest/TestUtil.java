package dk.bemyndigelsesregister.integrationtest;

public class TestUtil {

    public static String urlPrefix() {
        final String urlPrefix = System.getProperty("functionaltest.urlprefix");
        return urlPrefix != null ? urlPrefix : "http://localhost:8080";
    }
}
