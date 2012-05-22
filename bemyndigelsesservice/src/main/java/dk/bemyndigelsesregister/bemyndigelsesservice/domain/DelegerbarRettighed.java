package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegerbarRettighed extends ExternalIdentifiedDomainObject {
    private Arbejdsfunktion arbejdsfunktion;
    private Domaene domaene;
    private LinkedSystem system;

    public Arbejdsfunktion getArbejdsfunktion() {
        return arbejdsfunktion;
    }

    public void setArbejdsfunktion(Arbejdsfunktion arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }

    public Domaene getDomaene() {
        return domaene;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }

    public LinkedSystem getSystem() {
        return system;
    }

    public void setSystem(LinkedSystem system) {
        this.system = system;
    }
}
