package dk.bemyndigelsesregister.bemyndigelsesservice.ws;

import dk.nsi.bemyndigelse._2017._08._01.*;
import org.apache.log4j.Logger;

import javax.jws.WebService;

// TODO: @SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH) // MUST BE BOTH
@WebService(serviceName = "DelegationService", endpointInterface = "dk.nsi.bemyndigelse._2017._08._01.DelegationPortType", targetNamespace = "nsi.dk/bemyndigelse/2017/08/01/")
public class BemyndigelsesService_20170801 implements DelegationPortType {
    private static Logger logger = Logger.getLogger(BemyndigelsesService_20170801.class);


    @Override
    public PutMetadataResponse putMetadata20170801(PutMetadataRequest putMetadataRequest) {

        logger.info("putMetadata20170801 called");
        return null;
    }

    @Override
    public GetMetadataResponse getMetadata20170801(GetMetadataRequest getMetadataRequest) {
        logger.info("getMetadata20170801 called");

        return null;
    }

    @Override
    public GetDelegationsResponse getDelegations20170801(GetDelegationsRequest getDelegationsRequest) {
        logger.info("getDelegations20170801 called");

        return null;
    }

    @Override
    public CreateDelegationsResponse createDelegations20170801(CreateDelegationsRequest createDelegationsRequest) {
        logger.info("createDelegations20170801 called");

        return null;
    }

    @Override
    public DeleteDelegationsResponse deleteDelegations20170801(DeleteDelegationsRequest deleteDelegationsRequest) {
        logger.info("deleteDelegations20170801 called");

        return null;
    }
}
