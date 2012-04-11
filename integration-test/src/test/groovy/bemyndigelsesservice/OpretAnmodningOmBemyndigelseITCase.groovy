package bemyndigelsesservice

import org.junit.Test
import wslite.soap.SOAPClient

import shared.WebServiceSupport

import static org.junit.Assert.*

class OpretAnmodningOmBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canAccessMethod() {
        SOAPClient client = getClient()
        def response = client.send(
                SOAPAction: "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelse",
        ) {
            envelopeAttributes 'xmlns:web': 'http://web.bemyndigelsesservice.bemyndigelsesregister.dk/'
            body {
                "web:opretAnmodningOmBemyndigelse" {
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
