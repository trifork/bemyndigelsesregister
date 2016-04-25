package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class GetMetadataITCase extends WebServiceSupport {

    @Test
    public void willAllowRead() throws Exception {
        def response = send("GetMetadata") {
            "bms20160101:GetMetadataRequest" {
                "Domain"('trifork-test')
                "SystemId"('testsys')
            }
        }
        assert response
        assert 1 == response.GetMetadataResponse.Role.size()
    }
}
