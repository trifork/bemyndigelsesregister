package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OpretGodkendtBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class OpretGodkendtBemyndigelseRequest {
    @XmlElement(name = "Bemyndigende", required = true)
    private String bemyndigende;

    @XmlElement(name = "Bemyndigene", required = true)
    private String bemyndigede;

    @XmlElement(name = "BemyndigedeCVR", required = true)
    private String bemyndigedeCvr;

    @XmlElement(name = "System", required = true)
    private String system;

    @XmlElement(name = "arbejdsfunktion", required = true)
    private String arbejdsfunktion;

    @XmlElement(name = "rettighedskode", required = true)
    private String rettighedskode;

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

    public String getBemyndigedeCvr() {
        return bemyndigedeCvr;
    }

    public void setBemyndigedeCvr(String bemyndigedeCvr) {
        this.bemyndigedeCvr = bemyndigedeCvr;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(String arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public String getRettighedskode() {
        return rettighedskode;
    }

    public void setRettighedskode(String rettighedskode) {
        this.rettighedskode = rettighedskode;
    }

//</editor-fold>

}
