package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class HentMetadataITCase extends WebServiceSupport {

    @Test
    public void willAllowWhitelistedSystemsToRead() throws Exception {
        def response = send("hentMetadata") {
            "bms:HentMetadataRequest" {
                "Domaene"('trifork-test')
                "System"('Trifork test system')
            }
        }
        assert response
        assert 1 == response.HentMetadataResponse.Arbejdsfunktioner.size()
    }
}
