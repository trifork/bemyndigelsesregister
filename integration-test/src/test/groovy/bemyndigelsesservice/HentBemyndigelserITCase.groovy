package bemyndigelsesservice

import org.junit.Test
import wslite.soap.SOAPClient

import shared.WebServiceSupport

import static org.junit.Assert.*
import sosi.SOSIUtil
import org.w3c.dom.NodeList
import wslite.soap.SOAPFaultException

class HentBemyndigelserITCase extends WebServiceSupport {

    @Test
    public void willNotValidateOnBothBemyndigendeAndBemyndigedeParameter() {
        SOAPClient client = getClient()
        try {
            client.send(
                    SOAPAction: "http://nsi.dk/bemyndigelse/2012/05/01/hentBemyndigelser",
            ) {
                envelopeAttributes 'xmlns:web': 'http://nsi.dk/bemyndigelse/2012/05/01/',
                        'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
                header {
                    NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                    for (int i = 0; i < header.item(0).childNodes.length; i++) {
                        String headerItem = header.item(0).childNodes.item(i) as String
                        assert headerItem
                        mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                    }
                }
                body {
                    "web:HentBemyndigelserRequest" {
                        "Bemyndigende"('2006271866')
                        "Bemyndigede"('1010101010')
                    }
                }
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

    @Test
    public void willValidateOnBothBemyndigendeParameter() {
        SOAPClient client = getClient()
        def response = client.send(
                SOAPAction: "http://nsi.dk/bemyndigelse/2012/05/01/hentBemyndigelser",
        ) {
            envelopeAttributes 'xmlns:web': 'http://nsi.dk/bemyndigelse/2012/05/01/',
                    'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
            header {
                NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                for (int i = 0; i < header.item(0).childNodes.length; i++) {
                    String headerItem = header.item(0).childNodes.item(i) as String
                    assert headerItem
                    mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                }
            }
            body {
                "web:HentBemyndigelserRequest" {
                    "Bemyndigende"('2006271866')
                }
            }
        }
        assertFalse response.hasFault()
    }

    @Test
    public void willNotValidateOnNoParameters() {
        SOAPClient client = getClient()
        try {
            client.send(
                    SOAPAction: "http://nsi.dk/bemyndigelse/2012/05/01/hentBemyndigelser",
            ) {
                envelopeAttributes 'xmlns:web': 'http://nsi.dk/bemyndigelse/2012/05/01/',
                        'xmlns:sosi':"http://www.sosi.dk/sosi/2006/04/sosi-1.0.xsd"
                header {
                    NodeList header = SOSIUtil.getIdCard().getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header")
                    for (int i = 0; i < header.item(0).childNodes.length; i++) {
                        String headerItem = header.item(0).childNodes.item(i) as String
                        assert headerItem
                        mkp.yieldUnescaped headerItem.substring(headerItem.indexOf("?>") + 2)
                    }
                }
                body {
                    "web:HentBemyndigelserRequest"();
                }
            }
        } catch (SOAPFaultException e) {
            assertEquals "Validation error", e.fault.':faultstring'.text()
        }
    }

}
