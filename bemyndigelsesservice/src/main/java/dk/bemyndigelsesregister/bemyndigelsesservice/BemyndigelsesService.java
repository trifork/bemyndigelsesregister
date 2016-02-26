package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.nsi.bemyndigelse._2016._01._01.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.addressing.server.annotation.Action;

@Endpoint
public interface BemyndigelsesService {
    @PayloadRoot(localPart = "CreateDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2016/01/01/")
    @Action("http://nsi.dk/bemyndigelse/2016/01/01/createDelegations")
    @ResponsePayload
    CreateDelegationsResponse createDelegations(
            @RequestPayload CreateDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2016/01/01/")
    @Action("http://nsi.dk/bemyndigelse/2016/01/01/getDelegations")
    @ResponsePayload
    GetDelegationsResponse getDelegations(@RequestPayload GetDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "DeleteDelegationsRequest", namespace = "http://nsi.dk/bemyndigelse/2016/01/01/")
    @Action("http://nsi.dk/bemyndigelse/2016/01/01/deleteDelegations")
    @ResponsePayload
    DeleteDelegationsResponse deleteDelegations(@RequestPayload DeleteDelegationsRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GetMetadataRequest", namespace = "http://nsi.dk/bemyndigelse/2016/01/01/")
    @Action("http://nsi.dk/bemyndigelse/2016/01/01/getMetadata")
    @ResponsePayload
    GetMetadataResponse getMetadata(@RequestPayload GetMetadataRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "PutMetadataRequest", namespace = "http://nsi.dk/bemyndigelse/2016/01/01/")
    @Action("http://nsi.dk/bemyndigelse/2016/01/01/putMetadata")
    @ResponsePayload
    PutMetadataResponse putMetadata(@RequestPayload PutMetadataRequest request, SoapHeader soapHeader);
}
