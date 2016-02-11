package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "linked_system")
public class DelegatingSystem extends ExternalIdentifiedDomainObject {
    @Column(name = "beskrivelse")
    private String description;

    @ManyToOne
    @JoinColumn(name = "domaene_id")
    private Domain domain;

    public DelegatingSystem() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public static DelegatingSystem createForTest(final String domainId) {
        return new DelegatingSystem() {{
            setDomainId(domainId);
        }};
    }

}
