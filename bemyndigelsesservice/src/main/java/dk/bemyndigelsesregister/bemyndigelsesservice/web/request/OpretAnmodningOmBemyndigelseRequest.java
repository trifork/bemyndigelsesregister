package dk.bemyndigelsesregister.bemyndigelsesservice.web.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "opretAnmodningOmBemyndigelseRequest", namespace = "http://web.bemyndigelsesservice.bemyndigelsesregister.dk/")
public class OpretAnmodningOmBemyndigelseRequest {

    private String bemyndigedeCvr;
    private String bemyndigedeCpr;
    private String bemyndigendeCpr;
    private long arbejdsfunktionId;
    private long rettighedId;

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
