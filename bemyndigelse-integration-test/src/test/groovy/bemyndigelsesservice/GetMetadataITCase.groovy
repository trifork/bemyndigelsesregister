package bemyndigelsesservice_20170801

import org.junit.Test
import shared.WebServiceSupport

class GetMetadataITCase extends WebServiceSupport {

    @Test
    public void willAllowRead() throws Exception {
        def response = send("GetMetadata") {
            "bms20170801:GetMetadataRequest" {
                "bms20170801:Domain"('trifork-test')
                "bms20170801:SystemId"('testsys')
            }
        }
        assert response
        assert 1 == response.GetMetadataResponse.Role.size()
    }
}
