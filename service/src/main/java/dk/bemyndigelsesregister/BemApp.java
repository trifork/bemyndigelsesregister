package dk.bemyndigelsesregister;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class BemApp {
    private static final Logger log = LogManager.getLogger(BemApp.class);

    public static void main(String[] args) {
        log.info("BEM service initializing");

        // Use OpenJDK TransformerFactory instead of the old one from the Xalan dependency
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        SpringApplication.run(BemApp.class, args);
    }
}
