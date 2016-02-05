package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class LinkedSystem extends ExternalIdentifiedDomainObject10 {
    public LinkedSystem() {
    }

    @ManyToOne
    private Domaene domaene;

    public static LinkedSystem createForTest(final String kode) {
        return new LinkedSystem() {{
            setKode(kode);
        }};
    }

    public Domaene getDomaene() {
        return domaene;
    }

    public void setDomaene(Domaene domaene) {
        this.domaene = domaene;
    }
}
