package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HentBemyndigelserRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class HentBemyndigelserRequest {
    @XmlElement(name = "Bemyndigende", required = true)
    protected String bemyndigende;
    @XmlElement(name = "Bemyndigede", required = true)
    protected String bemyndigede;

//<editor-fold desc="GettersAndSetters">

    public String getBemyndigende() {
        return bemyndigende;
    }

    public void setBemyndigende(String bemyndigende) {
        this.bemyndigende = bemyndigende;
    }

    public String getBemyndigede() {
        return bemyndigede;
    }

    public void setBemyndigede(String bemyndigede) {
        this.bemyndigede = bemyndigede;
    }

//</editor-fold>
}
