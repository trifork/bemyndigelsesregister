package dk.bemyndigelsesregister.bemyndigelsesservice;

import dk.bemyndigelsesregister.bemyndigelsesservice.web.response.OpretAnmodningOmBemyndigelseResponse;
import dk.bemyndigelsesregister.bemyndigelsesservice.web.request.OpretAnmodningOmBemyndigelseRequest;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;

import javax.jws.WebService;

@WebService(serviceName = "bemyndigelsesservice.svc")
public interface BemyndigelsesService {

    @PayloadRoot(localPart="opretAnmodningOmBemyndigelseRequest", namespace="http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
    @Action("http://web.bemyndigelsesservice.bemyndigelsesregister.dk/opretAnmodningOmBemyndigelse")
    @ResponsePayload
    OpretAnmodningOmBemyndigelseResponse opretAnmodningOmBemyndigelse(@RequestPayload OpretAnmodningOmBemyndigelseRequest request);
}
