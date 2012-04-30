package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OpretAnmodningOmBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class OpretAnmodningOmBemyndigelseRequest {

    @XmlElement(name = "BemyndigedeCvr", required = true)
    protected String bemyndigedeCvr;

    @XmlElement(name = "BemyndigedeCpr", required = true)
    protected String bemyndigedeCpr;

    @XmlElement(name = "BemyndigendeCpr", required = true)
    protected String bemyndigendeCpr;

    @XmlElement(name = "Arbejdsfunktion", required = true)
    protected String arbejdsfunktion;

    @XmlElement(name = "Rettighed", required = true)
    protected String rettighed;

    @XmlElement(name = "System", required = true)
    protected String linkedSystem;

    public String getBemyndigedeCvr() {
        return bemyndigedeCvr;
    }

    public void setBemyndigedeCvr(String bemyndigedeCvr) {
        this.bemyndigedeCvr = bemyndigedeCvr;
    }

    public String getBemyndigedeCpr() {
        return bemyndigedeCpr;
    }

    public void setBemyndigedeCpr(String bemyndigedeCpr) {
        this.bemyndigedeCpr = bemyndigedeCpr;
    }

    public String getBemyndigendeCpr() {
        return bemyndigendeCpr;
    }

    public void setBemyndigendeCpr(String bemyndigendeCpr) {
        this.bemyndigendeCpr = bemyndigendeCpr;
    }

    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(String arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public String getRettighed() {
        return rettighed;
    }

    public void setRettighed(String rettighed) {
        this.rettighed = rettighed;
    }

    public String getLinkedSystem() {
        return linkedSystem;
    }

    public void setLinkedSystem(String linkedSystem) {
        this.linkedSystem = linkedSystem;
    }
}
