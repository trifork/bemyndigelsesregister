package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class Rettighed extends DomainObject {
    private Domaene domaene;
    private LinkedSystem system;
    private String rettighedskode;
    private String beskrivelse;

    public Rettighed() {
    }

    //<editor-fold desc="GettersAndSetters">

    public String getRettighedskode() {
        return rettighedskode;
    }

    public void setRettighedskode(String rettighedskode) {
        this.rettighedskode = rettighedskode;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setSystem(LinkedSystem system) {
        this.system = system;
    }

    public LinkedSystem getSystem() {
        return system;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    //</editor-fold>

}
