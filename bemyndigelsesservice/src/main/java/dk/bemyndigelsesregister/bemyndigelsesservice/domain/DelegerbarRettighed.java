package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class DelegerbarRettighed extends DomainObject {
    @ManyToOne
    private Arbejdsfunktion arbejdsfunktion;

    @ManyToOne
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
}
