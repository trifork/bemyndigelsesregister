package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.nsi.bemyndigelse._2012._05._01.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.addressing.server.annotation.Action;

@Endpoint
public interface BemyndigelsesService {

    @PayloadRoot(localPart = "OpretAnmodningOmBemyndigelseRequest", namespace = "http://nsi.dk/bemyndigelse/2012/05/01/")
    @Action("http://nsi.dk/bemyndigelse/2012/05/01/opretAnmodningOmBemyndigelser")
    @ResponsePayload
    OpretAnmodningOmBemyndigelseResponse opretAnmodningOmBemyndigelser(
            @RequestPayload OpretAnmodningOmBemyndigelseRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "GodkendBemyndigelserRequest", namespace = "http://nsi.dk/bemyndigelse/2012/05/01/")
    @Action("http://nsi.dk/bemyndigelse/2012/05/01/godkendBemyndigelser")
    @ResponsePayload
    GodkendBemyndigelseResponse godkendBemyndigelse(
            @RequestPayload GodkendBemyndigelseRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "OpretGodkendtBemyndigelseRequest", namespace = "http://nsi.dk/bemyndigelse/2012/05/01/")
    @Action("http://nsi.dk/bemyndigelse/2012/05/01/opretGodkendtBemyndigelse")
    @ResponsePayload
    OpretGodkendtBemyndigelseResponse opretGodkendtBemyndigelse(
            @RequestPayload OpretGodkendtBemyndigelseRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "SletBemyndigelseRequest", namespace = "http://nsi.dk/bemyndigelse/2012/05/01/")
    @Action("http://nsi.dk/bemyndigelse/2012/05/01/sletBemyndigelser")
    @ResponsePayload
    SletBemyndigelserResponse sletBemyndigelser(
            @RequestPayload SletBemyndigelserRequest request, SoapHeader soapHeader);

    @PayloadRoot(localPart = "HentBemyndigelserRequest", namespace = "http://nsi.dk/bemyndigelse/2012/05/01/")
    @Action("http://nsi.dk/bemyndigelse/2012/05/01/hentBemyndigelser")
    @ResponsePayload
    HentBemyndigelserResponse hentBemyndigelser(
            @RequestPayload HentBemyndigelserRequest request, SoapHeader soapHeader);
}
