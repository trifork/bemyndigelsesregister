package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class Arbejdsfunktion extends DomainObject {
    private Domaene domaene;
    private LinkedSystem system;
    private String arbejdsfunktion;
    private String beskrivelse;

    public Arbejdsfunktion() {
    }

    //<editor-fold desc="GettersAndSetters">

    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(String arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    public void setSystem(LinkedSystem system) {
        this.system = system;
    }

    public LinkedSystem getSystem() {
        return system;
    }
    //</editor-fold>

}
