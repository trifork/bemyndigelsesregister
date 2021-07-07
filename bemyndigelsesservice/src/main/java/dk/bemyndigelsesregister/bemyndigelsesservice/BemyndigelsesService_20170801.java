package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.nsi.bemyndigelse._2017._08._01.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.addressing.server.annotation.Action;

@Endpoint
public interface BemyndigelsesService_20170801 {
    @PayloadRoot(localPart = "CreateDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/createDelegations")
    @ResponsePayload
    CreateDelegationsResponse createDelegations(
            @RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/getDelegations")
    @ResponsePayload
    GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "DeleteDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/deleteDelegations")
    @ResponsePayload
    DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetMetadataRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/getMetadata")
    @ResponsePayload
    GetMetadataResponse getMetadata(@RequestPayload GetMetadataRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetAllMetadataRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/getAllMetadata")
    @ResponsePayload
    GetAllMetadataResponse getAllMetadata(@RequestPayload GetAllMetadataRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "PutMetadataRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/putMetadata")
    @ResponsePayload
    PutMetadataResponse putMetadata(@RequestPayload PutMetadataRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetExpirationInfoRequest", namespace = "http://nsi.dk/bemyndigelse/2017/08/01/")
    @Action("http://nsi.dk/bemyndigelse/2017/08/01/getExpirationInfo")
    @ResponsePayload
    GetExpirationInfoResponse getExpirationInfo(@RequestPayload GetExpirationInfoRequest request, SoapHeader soapHeader);
}
