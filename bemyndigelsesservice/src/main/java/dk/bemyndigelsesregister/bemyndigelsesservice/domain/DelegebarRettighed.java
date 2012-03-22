package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegebarRettighed extends DomainObject {
    private Domaene domaene;
    private LinkedSystem system;
    private Arbejdsfunktion arbejdsfunktion;
    private Rettighed Rettighedskode;

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
        return Rettighedskode;
    }

    public void setRettighedskode(Rettighed rettighedskode) {
        Rettighedskode = rettighedskode;
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
