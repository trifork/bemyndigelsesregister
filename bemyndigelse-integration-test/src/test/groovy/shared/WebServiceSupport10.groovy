package shared

import org.w3c.dom.NodeList
import sosi.SOSIUtil
import wslite.soap.SOAPClient
import wslite.soap.SOAPResponse

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix
import com.googlecode.flyway.core.Flyway
import org.springframework.jdbc.datasource.DriverManagerDataSource

abstract class WebServiceSupport10 {

    SOSIUtil sosiUtil = new SOSIUtil();

    protected SOSIUtil getSosiUtil() {
        return sosiUtil;
    }

    WebServiceSupport10() {
        Properties conf = new Properties();
        conf.load(WebServiceSupport10.class.getResourceAsStream("/bemyndigelse.properties"))
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                conf.getProperty("jdbc.url"),
                conf.getProperty("jdbc.username"),
                conf.getProperty("jdbc.password")
        );
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        Flyway flyway = new Flyway();
        flyway.setDisableInitCheck(true);
        flyway.setDataSource(dataSource);
        flyway.setTable("integrationtest_version");
        flyway.migrate()
    }

    protected SOAPClient getClient() {
        println "Creating client for ${urlPrefix()}"
        def client = new SOAPClient(urlPrefix() + "/")
        client.httpClient.proxy = createHttpProxy() ?: Proxy.NO_PROXY
        client
    }

    private Proxy createHttpProxy() {
        if (System.getProperty('http.proxyHost') && System.getProperty('http.proxyPort')) {
            println "Using proxy ${System.getProperty('http.proxyHost')}:${System.getProperty('http.proxyPort')}"
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getProperty('http.proxyHost'), Integer.parseInt(System.getProperty('http.proxyPort'))))
        }
        null
    }

    protected SOAPResponse send(String action, Closure content) {
        SOAPClient client = getClient()
        client.send(
                SOAPAction: "http://nsi.dk/bemyndigelse/2012/05/01/$action",
        ) {
            envelopeAttributes 'xmlns:bms': 'http://nsi.dk/bemyndigelse/2012/05/01/',
                    'xmlns:sosi': "http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = sosiUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                for (int i = 0; i < header.item(0).childNodes.length; i++) {
                    String headerItem = header.item(0).childNodes.item(i) as String
                    assert headerItem
                    mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                }
            }
            body(content)
        }
    }

}
