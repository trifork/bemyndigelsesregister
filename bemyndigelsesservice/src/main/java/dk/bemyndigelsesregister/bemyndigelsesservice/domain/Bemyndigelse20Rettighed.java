package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;

/**
 * BEM 2.0 rettighed til bemyndigelse
 * Created by obj on 02-02-2016.
 */
@Entity
@Table(name="bemyndigelse20_rettighed")
public class Bemyndigelse20Rettighed extends DomainObject {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bemyndigelse20_id")
    private Bemyndigelse20 bemyndigelse;

    private String rettighedKode;

    public Bemyndigelse20Rettighed() {
    }

    public Bemyndigelse20 getBemyndigelse() {
        return bemyndigelse;
    }

    public void setBemyndigelse(Bemyndigelse20 bemyndigelse) {
        this.bemyndigelse = bemyndigelse;
    }

    public String getRettighedKode() {
        return rettighedKode;
    }

    public void setRettighedKode(String rettighedKode) {
        this.rettighedKode = rettighedKode;
    }

    @Override
    public String toString() {
        return "Bemyndigelse20Rettighed{" +
                "rettighedKode='" + rettighedKode + '\'' +
                '}';
    }
}
