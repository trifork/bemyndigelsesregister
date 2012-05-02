package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.GodkendBemyndigelseRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.HentBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.OpretAnmodningOmBemyndigelseRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.SletBemyndigelserRequest;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.addressing.server.annotation.Action;

@Endpoint
public interface BemyndigelsesService {

    @PayloadRoot(localPart = "OpretAnmodningOmBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
    @Action("http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelser")
    @ResponsePayload
    OpretAnmodningOmBemyndigelseResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelseRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GodkendBemyndigelserRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
    @Action("http://web.bemyndigelsesservice.bemyndigelsesregister.dk/godkendBemyndigelser")
    @ResponsePayload
    GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "SletBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
    @Action("http://web.bemyndigelsesservice.bemyndigelsesregister.dk/sletBemyndigelser")
    @ResponsePayload
    SletBemyndigelserResponse sletBemyndigelser(
            @RequestPayload SletBemyndigelserRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "HentBemyndigelserRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
    @Action("http://web.bemyndigelsesservice.bemyndigelsesregister.dk/hentBemyndigelser")
    @ResponsePayload
    HentBemyndigelserResponse hentBemyndigelser(
            @RequestPayload HentBemyndigelserRequest request, SoapHeader soapHeader);
}
