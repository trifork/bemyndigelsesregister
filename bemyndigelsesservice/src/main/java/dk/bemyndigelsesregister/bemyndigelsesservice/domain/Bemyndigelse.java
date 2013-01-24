package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import dk.nsi.bemyndigelser._2012._04.Bemyndigelser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

import javax.persistence.Entity;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Entity
public class Bemyndigelse extends ExternalIdentifiedDomainObject {
    private static DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory= DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    private String bemyndigendeCpr;
    private String bemyndigedeCpr;
    private String bemyndigedeCvr;
    @OneToOne
    private LinkedSystem linkedSystem;
    @OneToOne
    private Arbejdsfunktion arbejdsfunktion;
    @OneToOne
    private Rettighed rettighed;
    
    @Enumerated(EnumType.STRING)
    private Status status;

    private DateTime godkendelsesdato;
    private DateTime gyldigFra;
    private DateTime gyldigTil;
    private int versionsid;

    public Bemyndigelse() {
    }

    //<editor-fold desc="GettersAndSetters">
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    /**
     * Bruges tilsyneladende kun til stamdata eksportering
     * TODO hvorfor er så mange felter null?
     * @return
     */
    public Bemyndigelser.Bemyndigelse toBemyndigelseType() {
        Bemyndigelser.Bemyndigelse type = new Bemyndigelser.Bemyndigelse();
        type.setBemyndigedeCpr(bemyndigedeCpr);
        type.setBemyndigedeCvr(bemyndigedeCvr);
        type.setBemyndigendeCpr(bemyndigendeCpr);
        // TODO perhaps there should be a field for this in the DB?
        type.setCreatedDate(null);
        type.setGodkendelsesdato(toXmlGregorianCalendar(godkendelsesdato));
        type.setStatus(status == Status.GODKENDT ? "Godkendt" : "Bestilt");
        type.setKode(getKode());
        type.setModifiedDate(toXmlGregorianCalendar(sidstModificeret));
        type.setArbejdsfunktion(arbejdsfunktion.getKode());
        type.setRettighed(rettighed.getKode());
        type.setSystem(linkedSystem.getKode());
        type.setValidFrom(toXmlGregorianCalendar(gyldigFra));
        type.setValidTo(toXmlGregorianCalendar(gyldigTil));
        return type;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }
}
