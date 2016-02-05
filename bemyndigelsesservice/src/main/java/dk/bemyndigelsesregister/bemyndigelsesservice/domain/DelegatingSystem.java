package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="linked_system")
public class DelegatingSystem extends ExternalIdentifiedDomainObject {
    public DelegatingSystem() {
    }

    @ManyToOne
    private Domain domain;

    public static DelegatingSystem createForTest(final String domainId) {
        return new DelegatingSystem() {{
            setDomainId(domainId);
        }};
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
