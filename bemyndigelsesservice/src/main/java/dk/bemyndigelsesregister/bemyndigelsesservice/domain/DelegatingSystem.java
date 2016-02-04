package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DelegatingSystem extends ExternalIdentifiedDomainObject {
    public DelegatingSystem() {
    }

    @ManyToOne
    private Domain domain;

    public static DelegatingSystem createForTest(final String id) {
        return new DelegatingSystem() {{
            setId(id);
        }};
    }

    public Domain getDomaene() {
        return domain;
    }

    public void setDomaene(Domaene domaene) {
        this.domain = domain;
    }
}
