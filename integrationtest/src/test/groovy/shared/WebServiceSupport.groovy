package shared

import org.w3c.dom.NodeList
import sosi.SOSIUtil
import wslite.soap.SOAPClient
import wslite.soap.SOAPResponse

import static dk.bemyndigelsesregister.integrationtest.TestUtil.urlPrefix

abstract class WebServiceSupport {

    SOSIUtil sosiUtil = new SOSIUtil();

    protected SOSIUtil getSosiUtil() {
        return sosiUtil;
    }

    WebServiceSupport() {
        Properties conf = new Properties();
        conf.load(WebServiceSupport.class.getResourceAsStream("/bemyndigelse.properties"))
    }

    protected SOAPClient getClient() {
        println "Creating client for ${urlPrefix()}/bem/BemyndigelsesService"
        def client = new SOAPClient(urlPrefix() + "/bem/BemyndigelsesService")
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
        return doSend(action, content, false);
    }

    protected SOAPResponse sendAsSystem(String action, Closure content) {
        return doSend(action, content, true);
    }

    private SOAPResponse doSend(String action, Closure content, boolean asSystem) {
        SOAPClient client = getClient()
        client.send(
                SOAPAction: "http://nsi.dk/bemyndigelse/2017/08/01/$action",
        ) {
            envelopeAttributes 'xmlns:bms20170801': 'http://nsi.dk/bemyndigelse/2017/08/01/',
                    'xmlns:sosi': "http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = (asSystem ? sosiUtil.getSystemIdCard() : sosiUtil.getIdCard()).getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
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
