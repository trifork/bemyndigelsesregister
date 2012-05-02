package dk.bemyndigelsesregister.bemyndigelsesservice.web.response;

import javax.xml.bind.annotation.*;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HentBemyndigelserResponse", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class HentBemyndigelserResponse {
    @XmlElement(name = "Bemyndigelser", required = true)
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
