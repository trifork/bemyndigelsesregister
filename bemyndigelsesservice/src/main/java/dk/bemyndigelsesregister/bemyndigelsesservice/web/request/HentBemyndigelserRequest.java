package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "HentBemyndigelserRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class HentBemyndigelserRequest {
    @XmlElement(name = "Bemyndigende", required = true)
    @XmlSchemaType(namespace = "http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/", name = "PersonCivilRegistrationIdentifier")
    protected String bemyndigende;
    @XmlElement(name = "Bemyndigede", required = true)
    @XmlSchemaType(namespace = "http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/", name = "PersonCivilRegistrationIdentifier")
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
