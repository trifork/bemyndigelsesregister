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

    @OneToOne
    private Rettighed rettighedskode;


    public Rettighed getRettighedskode() {
        return rettighedskode;
    }

    public void setRettighedskode(Rettighed rettighedskode) {
        this.rettighedskode = rettighedskode;
    }

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
