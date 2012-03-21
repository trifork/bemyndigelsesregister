package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;

@Entity
public class Rettighed extends DomainObject {
    private String domaene;
    private String system;
    private String rettighedskode;
    private String beskrivelse;

    public Rettighed() {
    }

    //<editor-fold desc="GettersAndSetters">
    public String getDomaene() {
        return domaene;
    }

    public void setDomaene(String domaene) {
        this.domaene = domaene;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

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
    //</editor-fold>
}
