package bemyndigelsesservice_20170801

import org.junit.Test
import shared.WebServiceSupport

class PutMetadataITCase extends WebServiceSupport {

    @Test
    public void willAllowWhitelistedSystemsToWrite() throws Exception {
        def response = sendAsSystem("PutMetadata") {
            "bms20170801:PutMetadataRequest" {
                "bms20170801:Domain"('trifork-test')
                "bms20170801:SystemId"('testsys')
                "bms20170801:SystemLongName"('Trifork testsystem')
                "bms20170801:Permission" {
                    "bms20170801:PermissionId"('Read')
                    "bms20170801:PermissionDescription"('Læse i journal')
                }
                "bms20170801:EnableAsteriskPermission"('false')
                "bms20170801:Role" {
                    "bms20170801:RoleId"('Laege')
                    "bms20170801:RoleDescription"('En praktiserende læge')
                    "bms20170801:DelegatablePermissions" {
                        "bms20170801:PermissionId"('Read')
                    }
                }
            }
        }
        assert response
        assert null != response.PutMetadataResponse
    }
}