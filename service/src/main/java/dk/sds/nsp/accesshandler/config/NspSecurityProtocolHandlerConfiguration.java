package dk.sds.nsp.accesshandler.config;

// This class overrides the accesshandler configuration by placing it first on the classpath
// This allows the certificate properties to be defined in application.properties

public class NspSecurityProtocolHandlerConfiguration {
    public static String signingKeystoreFilename;
    public static String signingKeystorePassword;
    public static String signingKeystoreAlias;

    public String getSigningKeystoreFilename() {
        return signingKeystoreFilename;
    }

    public String getSigningKeystorePassword() {
        return signingKeystorePassword;
    }

    public String getSigningKeystoreAlias() {
        return signingKeystoreAlias;
    }
}
