package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegebarRettighed extends DomainObject {
    private String domaene;
    private String system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed Rettighedskode;

    public DelegebarRettighed(Long id) {
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

    public Arbejdsfunktion getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(Arbejdsfunktion arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public Rettighed getRettighedskode() {
        return Rettighedskode;
    }

    public void setRettighedskode(Rettighed rettighedskode) {
        Rettighedskode = rettighedskode;
    }
    //</editor-fold>
}
