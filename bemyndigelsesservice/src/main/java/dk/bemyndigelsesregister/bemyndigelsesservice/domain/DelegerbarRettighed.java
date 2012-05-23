package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class DelegerbarRettighed extends ExternalIdentifiedDomainObject {
    @OneToOne
    private Arbejdsfunktion arbejdsfunktion;
    @OneToOne
    private Domaene domaene;
    @OneToOne
    private LinkedSystem linkedSystem;

    //TODO: Hvad med Rettighedkode_id?

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
