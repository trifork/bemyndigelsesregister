package dk.bemyndigelsesregister.bemyndigelsesservice.web.response;

import java.util.Collection;

public class HentBemyndigelserResponse {
    protected Collection<String> bemyndigelser;
//<editor-fold desc="GettersAndSetters">

    public Collection<String> getBemyndigelser() {
        return bemyndigelser;
    }

    public void setBemyndigelser(Collection<String> bemyndigelser) {
        this.bemyndigelser = bemyndigelser;
    }

//</editor-fold>
}
