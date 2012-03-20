package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class Arbejdsfunktion extends DomainObject {
    private String domaene;
    private String system;
    private String arbejdsfunktion;
    private String beskrivelse;

    public Arbejdsfunktion(Long id) {
        super(id);
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
    //</editor-fold>
}
