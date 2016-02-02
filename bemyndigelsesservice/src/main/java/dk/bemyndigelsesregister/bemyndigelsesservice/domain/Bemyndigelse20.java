package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Set;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
 */

@Entity
public class Bemyndigelse20 extends ExternalIdentifiedDomainObject {
    private String bemyndigendeCpr;
    private String bemyndigedeCpr;
    private String bemyndigedeCvr;
    private String linkedSystemKode;
    private String arbejdsfunktionKode;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Bemyndigelse20Rettighed> rettigheder;

    @Enumerated(EnumType.STRING)
    private Status status;

    private DateTime godkendelsesdato;
    private DateTime gyldigFra;
    private DateTime gyldigTil;
    private int versionsid;

    public Bemyndigelse20() {
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

    public Set<Bemyndigelse20Rettighed> getRettigheder() {
        return rettigheder;
    }

    public void setRettigheder(Set<Bemyndigelse20Rettighed> rettigheder) {
        this.rettigheder = rettigheder;
    }

    @Override
    public String toString() {
        Set<Bemyndigelse20Rettighed> rettigheder = this.getRettigheder();
        for (Bemyndigelse20Rettighed r : rettigheder)
            System.out.println(r.getRettighedKode());


        return "Bemyndigelse20{" +
                "bemyndigendeCpr='" + bemyndigendeCpr + '\'' +
                ", bemyndigedeCpr='" + bemyndigedeCpr + '\'' +
                ", bemyndigedeCvr='" + bemyndigedeCvr + '\'' +
                ", linkedSystemKode='" + linkedSystemKode + '\'' +
                ", arbejdsfunktionKode='" + arbejdsfunktionKode + '\'' +
                ", rettigheder=" + rettigheder +
                ", status=" + status +
                ", godkendelsesdato=" + godkendelsesdato +
                ", gyldigFra=" + gyldigFra +
                ", gyldigTil=" + gyldigTil +
                ", versionsid=" + versionsid +
                '}';
    }
}
