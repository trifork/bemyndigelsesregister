package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class DelegebarRettighed extends DomainObject {
    private Domaene domaene;
    private LinkedSystem linkedSystem;
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

    public void setLinkedSystem(LinkedSystem linkedSystem) {
        this.linkedSystem = linkedSystem;
    }

    public LinkedSystem getLinkedSystem() {
        return linkedSystem;
    }
    //</editor-fold>

}
