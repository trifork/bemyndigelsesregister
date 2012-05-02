package dk.bemyndigelsesregister.bemyndigelsesservice.web.response;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GodkendBemyndigelseResponse", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class GodkendBemyndigelseResponse {
    @XmlElement(name = "GodkendtBemyndigelsesKode", required = true)
    protected String godkendtBemyndigelsesKode;

//<editor-fold desc="GettersAndSetters">

    public String getGodkendtBemyndigelsesKode() {
        return godkendtBemyndigelsesKode;
    }

    public void setGodkendtBemyndigelsesKode(String godkendtBemyndigelsesKode) {
        this.godkendtBemyndigelsesKode = godkendtBemyndigelsesKode;
    }

//</editor-fold>
}
