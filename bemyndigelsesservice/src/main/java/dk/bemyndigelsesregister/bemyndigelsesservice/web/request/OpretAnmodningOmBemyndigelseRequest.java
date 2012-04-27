package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "opretAnmodningOmBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class OpretAnmodningOmBemyndigelseRequest {

    @XmlElement(name = "bemyndigedeCvr", required = true)
    protected String bemyndigedeCvr;

    @XmlElement(name = "bemyndigedeCpr", required = true)
    protected String bemyndigedeCpr;

    @XmlElement(name = "bemyndigendeCpr", required = true)
    protected String bemyndigendeCpr;

    @XmlElement(name = "arbejdsfunktionId", required = true)
    protected long arbejdsfunktionId;

    @XmlElement(name = "rettighedId", required = true)
    protected long rettighedId;

    public void setBemyndigedeCvr(String bemyndigedeCvr) {
        this.bemyndigedeCvr = bemyndigedeCvr;
    }

    public void setBemyndigedeCpr(String bemyndigedeCpr) {
        this.bemyndigedeCpr = bemyndigedeCpr;
    }

    public void setBemyndigendeCpr(String bemyndigendeCpr) {
        this.bemyndigendeCpr = bemyndigendeCpr;
    }

    public void setArbejdsfunktionId(long arbejdsfunktionId) {
        this.arbejdsfunktionId = arbejdsfunktionId;
    }

    public void setRettighedId(long rettighedId) {
        this.rettighedId = rettighedId;
    }

    public String getBemyndigedeCvr() {
        return bemyndigedeCvr;
    }

    public String getBemyndigedeCpr() {
        return bemyndigedeCpr;
    }

    public String getBemyndigendeCpr() {
        return bemyndigendeCpr;
    }

    public long getArbejdsfunktionId() {
        return arbejdsfunktionId;
    }

    public long getRettighedId() {
        return rettighedId;
    }
}
