package bemyndigelsesservice

import org.junit.Test
import shared.WebServiceSupport

class PutMetadataITCase extends WebServiceSupport {

    @Test
    public void willAllowWhitelistedSystemsToWrite() throws Exception {
        def response = sendAsSystem("PutMetadata") {
            "bms20160101:PutMetadataRequest" {
                "Domain"('trifork-test')
                "SystemId"('testsys')
                "SystemLongName"("'Trifork testsystem")
                "Role" {
                    "RoleId"('Laege')
                    "RoleDescription"('En praktiserende læge')
                }
                "Permission" {
                    "PermissionId"('Read')
                    "PermissionDescription"('Læse i journal')
                }
            }
        }
        assert response
        assert null != response.PutMetadataResponse
    }
}