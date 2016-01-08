package dk.bemyndigelsesregister.notproduction.validateWikiExamples;

import dk.bemyndigelsesregister.notproduction.validateWikiExamples.xmlvalidator.SchemaValidator;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class XMLValidator {

    private String assumedNamespace;
    private SchemaValidator validator = new SchemaValidator();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java XMLValidator <xmlUrl> [<namespace>]");
            System.exit(1);
        }

        String url = args[0];
        String assumedNamespace = null;
        if (args.length > 1) {
            assumedNamespace = args[1];
        }
        boolean result;

        try {
            result = new XMLValidator(assumedNamespace).validateXml(url);
        } catch (Exception e) {
            System.out.println("Exception while processing XML url: " + url);
            e.printStackTrace(System.out);
            result = false;
        }

        if (!result) {
            System.exit(1);
        }
    }

    public XMLValidator(String assumedNamespace) {
        this.assumedNamespace = assumedNamespace;
    }

    /** @return true for success, false if there were errors. */
    public boolean validateXml(String url) throws IOException {
        return validateXml(new URL(url));
    }

    /** @return true for success, false if there were errors. */
    public boolean validateXml(URL url) throws IOException {
        boolean result = true;

        // Read XML file
        String xml;
        try (InputStream in = url.openStream()) {
            xml = IOUtils.toString(in);
        }

        xml = insertNamespace(xml); // used in FMK to generalize code snippets for several service versions

        if (xml.contains("2016/01/01")) {
            System.out.println("    WARNING: XML uses draft 2.0 namespace (http://www.nsi/bemyndigelser/2016/01/01)");
        }

        
        // Validate
        try {
            validator.validate(xml);
        } catch (SAXParseException e) {
            System.out.println("    Validation error: " + e.getMessage() + "  Line " + e.getLineNumber() + " column " + e.getColumnNumber());
        } catch (SAXException e) {
            System.out.println("    Exception: " + e.getMessage());
        }

        return result;
    }

    private static Pattern firstElementPattern = Pattern.compile("^\\s*<\\s*([A-Za-z0-9]+:?[A-Za-z0-9]*)([^/>]*)(/?)>");

    private String insertNamespace(String xml) {

        Matcher m = firstElementPattern.matcher(xml);
        if (m.find()) {
            String matchString = m.group(0);
            String elementName = m.group(1);
            String postfix = m.group(2);
            String endtag = m.group(3);

            if (postfix.trim().isEmpty()) {
                String newElement = "<" + elementName + " " + assumedNamespace + endtag + ">";
                String newXml = newElement + xml.substring(matchString.length());
                //System.out.println("    Replaced '" + matchString + "' with '" + newElement + "'");
                return newXml;
            }
        }
        return xml;
    }


}
