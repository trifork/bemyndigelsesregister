package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

public class Bemyndigelse extends DomainObject {
    private String kode;
    private String bemyndigendeCpr;
    private String bemyndigedeCpr;
    private String bemyndigedeCvr;
    private String system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed rettighed;
    private StatusType status;
    private DateTime godkendelsesdato;
    private DateTime gyldigFra;
    private DateTime gyldigTil;
    private int versionsid;

    public Bemyndigelse() {
    }

    public Bemyndigelse() {
        super(null);
    }

    //<editor-fold desc="GettersAndSetters">
    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getBemyndigendeCpr() {
        return bemyndigendeCpr;
    }

    public void setBemyndigendeCpr(String bemyndigendeCpr) {
        this.bemyndigendeCpr = bemyndigendeCpr;
    }

    public String getBemyndigedeCpr() {
        return bemyndigedeCpr;
    }

    public void setBemyndigedeCpr(String bemyndigedeCpr) {
        this.bemyndigedeCpr = bemyndigedeCpr;
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

    public Arbejdsfunktion getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(Arbejdsfunktion arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public Rettighed getRettighed() {
        return rettighed;
    }

    public void setRettighed(Rettighed rettighed) {
        this.rettighed = rettighed;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public DateTime getGodkendelsesdato() {
        return godkendelsesdato;
    }

    public void setGodkendelsesdato(DateTime godkendelsesdato) {
        this.godkendelsesdato = godkendelsesdato;
    }

    public DateTime getGyldigFra() {
        return gyldigFra;
    }

    public void setGyldigFra(DateTime gyldigFra) {
        this.gyldigFra = gyldigFra;
    }

    public DateTime getGyldigTil() {
        return gyldigTil;
    }

    public void setGyldigTil(DateTime gyldigTil) {
        this.gyldigTil = gyldigTil;
    }

    public int getVersionsid() {
        return versionsid;
    }

    public void setVersionsid(int versionsid) {
        this.versionsid = versionsid;
    }
    //</editor-fold>
}
