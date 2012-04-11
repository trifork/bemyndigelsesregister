package dk.bemyndigelsesregister.bemyndigelsesservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "bemyndigelsesservice.svc")
public interface BemyndigelsesService {
    @WebMethod
    void opretAnmodningOmBemyndigelse(
            @WebParam(name = "bemyndigedeCpr") String bemyndigedeCpr,
            @WebParam(name = "bemyndigedeCvr") String bemyndigedeCvr,
            @WebParam(name = "bemyndigendeCpr") String bemyndigendeCpr,
            @WebParam(name = "arbejdsfunktionId") long arbejdsfunktionId,
            @WebParam(name = "rettighedId") long rettighedId);
}
