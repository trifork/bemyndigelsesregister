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
public class Bemyndigelse extends ExternalIdentifiedDomainObject10 {
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
    private String linkedSystemKode;
    private String arbejdsfunktionKode;
    private String rettighedKode;
    
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

    public String getLinkedSystemKode() {
        return linkedSystemKode;
    }

    public void setLinkedSystemKode(String linkedSystemKode) {
        this.linkedSystemKode = linkedSystemKode;
    }

    public String getArbejdsfunktionKode() {
        return arbejdsfunktionKode;
    }

    public void setArbejdsfunktionKode(String arbejdsfunktionKode) {
        this.arbejdsfunktionKode = arbejdsfunktionKode;
    }

    public String getRettighedKode() {
        return rettighedKode;
    }

    public void setRettighedKode(String rettighedKode) {
        this.rettighedKode = rettighedKode;
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
        type.setArbejdsfunktion(arbejdsfunktionKode);
        type.setRettighed(rettighedKode);
        type.setSystem(linkedSystemKode);
        type.setValidFrom(toXmlGregorianCalendar(gyldigFra));
        type.setValidTo(toXmlGregorianCalendar(gyldigTil));
        return type;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
    }
}
