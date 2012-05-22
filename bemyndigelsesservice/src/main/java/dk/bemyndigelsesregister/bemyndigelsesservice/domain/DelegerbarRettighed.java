package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegerbarRettighed extends ExternalIdentifiedDomainObject {
    private Arbejdsfunktion arbejdsfunktion;
    private Domaene domaene;
    private LinkedSystem linkedSystem;

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

    public LinkedSystem getLinkedSystem() {
        return linkedSystem;
    }

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.linkedSystem = linkedSystem;
    }
}
