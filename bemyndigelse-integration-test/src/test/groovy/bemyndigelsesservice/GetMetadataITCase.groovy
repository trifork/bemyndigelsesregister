package bemyndigelsesservice

import org.junit.Ignore
import org.junit.Test
import shared.WebServiceSupport

class GetMetadataITCase extends WebServiceSupport {

    @Ignore
    @Test
    public void willAllowWhitelistedSystemsToRead() throws Exception {
        def response = send("GetMetadata") {
            "bms:GetMetadataRequest" {
                "Domain"('trifork-test')
                "System"('testsys')
            }
        }
        assert response
        assert 1 == response.HentMetadataResponse.Arbejdsfunktioner.size()
    }
}
