package dk.bemyndigelsesregister.bemyndigelsesservice.web.response;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OpretGodkendtBemyndigelseResponse", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class OpretGodkendtBemyndigelseResponse {
    @XmlElement(name = "GodkendtBemyndigelsesKode", required = true)
    private String godkendtBemyndigelsesKode;

//<editor-fold desc="GettersAndSetters">

    public String getGodkendtBemyndigelsesKode() {
        return godkendtBemyndigelsesKode;
    }

    public void setGodkendtBemyndigelsesKode(String godkendtBemyndigelsesKode) {
        this.godkendtBemyndigelsesKode = godkendtBemyndigelsesKode;
    }

//</editor-fold>
}
