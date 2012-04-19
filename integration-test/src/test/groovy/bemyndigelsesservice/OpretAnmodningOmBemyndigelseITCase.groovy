package bemyndigelsesservice

import org.junit.Test
import wslite.soap.SOAPClient

import shared.WebServiceSupport

import static org.junit.Assert.*
import wslite.soap.SOAPFaultException

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canAccessMethod() {
        SOAPClient client = getClient()
        String messageID = UUID.randomUUID().toString()
        def response = client.send(
                SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelse",
        ) {
            envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/'
            header {
                Header (xmlns:'http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd') {
                    Linking {
                        FlowID("FlowID")
                        MessageID(messageID)
                    }
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
