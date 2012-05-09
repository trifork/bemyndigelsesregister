package bemyndigelsesservice

import shared.WebServiceSupport
import org.junit.Test

class OpretGodkendtBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canCreateApprovedBemyndigelse() {
        def response = send("opretGodkendteBemyndigelser") {
            "bms:OpretGodkendteBemyndigelserRequest" {
                "Bemyndigelse" {
                    "Bemyndigende"('2006271866')
                    "Bemyndigede"('1010101010')
                    "BemyndigedeCVR"('10101010')
                    "System"('Trifork test system')
                    "Arbejdsfunktion"('Laege')
                    "Rettighed"('R01')
                }
            }
        }

        assert response
        assert 1 == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse.size()
        assert response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].Kode.text()
        assert '2006271866' == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].Bemyndigende.text()
        assert '1010101010' == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].Bemyndigede.text()
    }
}
