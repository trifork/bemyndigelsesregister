package dk.nsi.sdm4.bemyndigelse.domain;

import dk.nsi.sdm2.core.domain.AbstractRecord;
import org.joda.time.DateTime;

import javax.persistence.Entity;

@Entity
public class BemyndigelseRecord extends AbstractRecord {
    private String kode;
    private String bemyndigendeCPR;
    private String bemyndigedeCPR;
    private String bemyndigedeCVR;
    private String system;
    private String arbejdsfunktion;
    private String rettighed;
    private String status;
    private String godkendelsesDato;
    private String oprettelsesDato;
    private String modificeretDato;
    private String gyldigFraDato;
    private String gyldigTilDato;

    public static BemyndigelseRecord createFrom(final Bemyndigelse bemyndigelse) {
        return new BemyndigelseRecord() {{
            setKode(bemyndigelse.kode);
            //TODO: finish mapping
        }};
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getBemyndigendeCPR() {
        return bemyndigendeCPR;
    }

    public void setBemyndigendeCPR(String bemyndigendeCPR) {
        this.bemyndigendeCPR = bemyndigendeCPR;
    }

    public String getBemyndigedeCPR() {
        return bemyndigedeCPR;
    }

    public void setBemyndigedeCPR(String bemyndigedeCPR) {
        this.bemyndigedeCPR = bemyndigedeCPR;
    }

    public String getBemyndigedeCVR() {
        return bemyndigedeCVR;
    }

    public void setBemyndigedeCVR(String bemyndigedeCVR) {
        this.bemyndigedeCVR = bemyndigedeCVR;
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

    public String getRettighed() {
        return rettighed;
    }

    public void setRettighed(String rettighed) {
        this.rettighed = rettighed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGodkendelsesDato() {
        return godkendelsesDato;
    }

    public void setGodkendelsesDato(String godkendelsesDato) {
        this.godkendelsesDato = godkendelsesDato;
    }

    public String getOprettelsesDato() {
        return oprettelsesDato;
    }

    public void setOprettelsesDato(String oprettelsesDato) {
        this.oprettelsesDato = oprettelsesDato;
    }

    public String getModificeretDato() {
        return modificeretDato;
    }

    public void setModificeretDato(String modificeretDato) {
        this.modificeretDato = modificeretDato;
    }

    public String getGyldigFraDato() {
        return gyldigFraDato;
    }

    public void setGyldigFraDato(String gyldigFraDato) {
        this.gyldigFraDato = gyldigFraDato;
    }

    public String getGyldigTilDato() {
        return gyldigTilDato;
    }

    public void setGyldigTilDato(String gyldigTilDato) {
        this.gyldigTilDato = gyldigTilDato;
    }
}
