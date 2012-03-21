package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Bemyndigelse;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class BemyndigelsesService {

    @WebMethod
    public String hello() {
        return "OK";
    }

    @WebMethod
    public void opretAnmodningOmBemyndigelse(String bemyndigedeCpr, String bemyndigedeCvr, String bemyndigendeCpr) {
        final Bemyndigelse bemyndigelse = new Bemyndigelse();
        bemyndigelse.setBemyndigedeCpr(bemyndigedeCpr);
        bemyndigelse.setBemyndigedeCvr(bemyndigedeCvr);
        bemyndigelse.setBemyndigendeCpr(bemyndigendeCpr);

    }
}
