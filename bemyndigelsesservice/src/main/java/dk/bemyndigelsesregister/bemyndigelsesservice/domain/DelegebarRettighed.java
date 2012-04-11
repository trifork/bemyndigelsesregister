package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegebarRettighed extends DomainObject {
    private Domaene domaene;
    private LinkedSystem system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed rettighedskode;

    public DelegebarRettighed() {
    }

    //<editor-fold desc="GettersAndSetters">

    public Arbejdsfunktion getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(Arbejdsfunktion arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public Rettighed getRettighedskode() {
        return rettighedskode;
    }

    public void setRettighedskode(Rettighed rettighedskode) {
        this.rettighedskode = rettighedskode;
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
