package bemyndigelsesservice

import org.junit.Test
import wslite.soap.SOAPClient

import shared.WebServiceSupport

import static org.junit.Assert.*
import sosi.SOSIUtil
import org.w3c.dom.NodeList

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canAccessMethod() {
        SOAPClient client = getClient()
        def response = client.send(
                SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelse",
        ) {
            envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/',
                    'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                println "header = $header"
                for (int i = 0; i < header.item(0).childNodes.length; i++) {
                    String headerItem = header.item(0).childNodes.item(i) as String
                    assert headerItem
                    println "header[i] = ${headerItem}"
                    mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                }
            }
            body {
                "web:opretAnmodningOmBemyndigelseRequest" {
                    bemyndigedeCpr(1)
                    bemyndigedeCvr(2)
                    bemyndigendeCpr(3)
                    arbejdsfunktionId(1)
                    rettighedId(1)
                }
            }
        }
        assertFalse response.hasFault()
    }
}
