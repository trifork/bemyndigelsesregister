package bemyndigelsesservice

import shared.WebServiceSupport
import org.junit.Test

class OpretGodkendtBemyndigelseITCase extends WebServiceSupport {

    @Test
    public void canCreateApprovedBemyndigelse() {
        def response = send("opretGodkendteBemyndigelser") {
            "bms:OpretGodkendteBemyndigelserRequest" {
                "Bemyndigelse" {
                    "BemyndigendeCpr"('2006271866')
                    "BemyndigedeCpr"('1010101010')
                    "BemyndigedeCvr"('10101010')
                    "System"('triforktest')
                    "Arbejdsfunktion"('Laege')
                    "Rettighed"('R01')
                }
            }
        }

        assert response
        assert 1 == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse.size()
        assert response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].Kode.text()
        assert '2006271866' == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].BemyndigendeCpr.text()
        assert '1010101010' == response.OpretGodkendteBemyndigelserResponse.Bemyndigelse[0].BemyndigedeCpr.text()
    }
}
