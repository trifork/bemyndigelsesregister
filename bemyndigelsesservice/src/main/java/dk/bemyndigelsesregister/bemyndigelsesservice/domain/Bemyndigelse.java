package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.OneToOne;

import javax.persistence.Entity;

@Entity
public class Bemyndigelse extends DomainObject {
    private String kode;
    private String bemyndigendeCpr;
    private String bemyndigedeCpr;
    private String bemyndigedeCvr;
    @OneToOne
    private LinkedSystem linkedSystem;
    @OneToOne
    private Arbejdsfunktion arbejdsfunktion;
    @OneToOne
    private Rettighed rettighed;
    @OneToOne
    private StatusType status;

    private DateTime godkendelsesdato;
    private DateTime gyldigFra;
    private DateTime gyldigTil;
    private int versionsid;

    public Bemyndigelse() {
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

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.linkedSystem = linkedSystem;
    }

    public LinkedSystem getLinkedSystem() {
        return linkedSystem;
    }
    //</editor-fold>
}