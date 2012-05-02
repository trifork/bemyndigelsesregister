package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GodkendBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class GodkendBemyndigelseRequest {
    @XmlElement(name = "BemyndigelsesKode", required = true)
    protected String bemyndigelsesKode;

//<editor-fold desc="GettersAndSetters">

    public String getBemyndigelsesKode() {
        return bemyndigelsesKode;
    }

    public void setBemyndigelsesKode(String bemyndigelsesKode) {
        this.bemyndigelsesKode = bemyndigelsesKode;
    }

//</editor-fold>

}
